package ko.kr.kms.covid19_inoculation_checklist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ramotion.foldingcell.FoldingCell;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ko.kr.kms.covid19_inoculation_checklist.fragment.CenteredTextFragment;
import ko.kr.kms.covid19_inoculation_checklist.menu.DrawerAdapter;
import ko.kr.kms.covid19_inoculation_checklist.menu.DrawerItem;
import ko.kr.kms.covid19_inoculation_checklist.menu.SimpleItem;
import ko.kr.kms.covid19_inoculation_checklist.menu.SpaceItem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CheckListViewActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private long pressedTime;

    private static final int POS_UNCONFIRMEDLIST = 0;
    private static final int POS_CONFIRMEDLIST = 1;
    private static final int POS_IMPORT = 3;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    private DrawerAdapter slidingRootDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_view);

        // get our list view
        ListView theListView = findViewById(R.id.checkListView);

        // prepare elements to display
        final ArrayList<Item> items = Item.getTestingList();

        // add custom btn handler to first list item
        items.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
            }
        });

        // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
        final FoldingCellListAdapter adapter = new FoldingCellListAdapter(this, items);

        // add default btn handler for each request btn on each item if custom handler not found
        adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show();
            }
        });

        // set elements to adapter
        theListView.setAdapter(adapter);

        // set on click event listener to list view
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });

        /*
         * SlidingRootNav
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        slidingRootDrawerAdapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_UNCONFIRMEDLIST).setChecked(true),
                createItemFor(POS_CONFIRMEDLIST),
                new SpaceItem(48),
                createItemFor(POS_IMPORT)));
        slidingRootDrawerAdapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(slidingRootDrawerAdapter);

        slidingRootDrawerAdapter.setSelected(POS_UNCONFIRMEDLIST);
    }

    @Override
    public void onItemSelected(int position) {
        switch (position) {
            case POS_UNCONFIRMEDLIST:
                break;

            case POS_CONFIRMEDLIST:
                break;

            case POS_IMPORT:
                Util.verifyStoragePermissions(this);

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                startActivityForResult(Intent.createChooser(intent, "Open Excel File"), 1);
                break;

            default:
                break;
        }

        slidingRootNav.closeMenu();
        Fragment selectedScreen = CenteredTextFragment.createFor(screenTitles[position]);
        showFragment(selectedScreen);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    if (data != null) {
                        getCheckList(data.getData());
                    } else {
                        Toast.makeText(this, "파일 읽기에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    Toast.makeText(this, "파일 읽기에 실패했습니다", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        slidingRootDrawerAdapter.setSelected(POS_UNCONFIRMEDLIST);
    }

    public void getCheckList(Uri pathUri) {
        try {
            Log.d(":: DEBUG", readExcel(pathUri).toArray()[0].toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "리스트 불러오기에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withMaxHeight(96)
                .withMaxWidth(96)
                .withIconTint(color(R.color.black_overlay))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    public ArrayList<Item> readExcel(Uri listUri) {
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        ArrayList<Item> list = new ArrayList<Item>();

        try {
            File file = Util.getImageFile(this, listUri);

            FileInputStream fileInputStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            int rowIndex = 0;
            int colIndex = 0;

            // 시트 수
            XSSFSheet sheet = workbook.getSheetAt(0);
            // 행의 수
            int rows = sheet.getPhysicalNumberOfRows();
            for (rowIndex = 2; rowIndex < rows; rowIndex++) {
                // 행 읽기
                XSSFRow row = sheet.getRow(rowIndex);
                XSSFCell cell = row.getCell(2);

                Item item = Item.getInstance();

                item.setName(cell.getStringCellValue());
                Log.d(":: Data", item.toString());

                list.add(item);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void onBackPressed() {
        if (pressedTime == 0) {
            Toast.makeText(this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();
        } else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if (seconds > 2000) {
                pressedTime = 0;
            } else {
                finish();
            }
        }
    }
}