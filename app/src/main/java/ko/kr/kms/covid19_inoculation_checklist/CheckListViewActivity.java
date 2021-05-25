package ko.kr.kms.covid19_inoculation_checklist;

import android.app.Activity;
import android.content.ContentValues;
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

import com.amitshekhar.DebugDB;
import com.ramotion.foldingcell.FoldingCell;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ko.kr.kms.covid19_inoculation_checklist.database.DBHelper;
import ko.kr.kms.covid19_inoculation_checklist.database.Database;
import ko.kr.kms.covid19_inoculation_checklist.fragment.CenteredTextFragment;
import ko.kr.kms.covid19_inoculation_checklist.menu.DrawerAdapter;
import ko.kr.kms.covid19_inoculation_checklist.menu.DrawerItem;
import ko.kr.kms.covid19_inoculation_checklist.menu.SimpleItem;
import ko.kr.kms.covid19_inoculation_checklist.menu.SpaceItem;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CheckListViewActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private long pressedTime;

    private static final int POS_UNCONFIRMED_LIST = 0;
    private static final int POS_CONFIRMED_LIST = 1;
    private static final int POS_IMPORT = 3;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private FoldingCellListAdapter foldingCellListAdapter;
    ListView checkListView;

    private SlidingRootNav slidingRootNav;
    private DrawerAdapter slidingRootDrawerAdapter;

    private ArrayList<Item> items;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_view);

        // get our list view
        checkListView = findViewById(R.id.checkListView);

        Database.getInstance().createDatabase(this);
        dbHelper = new DBHelper(this);
        DebugDB.getAddressLog();

        // prepare elements to display
        items = Item.getTestingList();

        // add custom btn handler to first list item
        items.get(0).setBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
            }
        });

        // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
        foldingCellListAdapter = new FoldingCellListAdapter(this, items);

        // add default btn handler for each request btn on each item if custom handler not found
        foldingCellListAdapter.setDefaultBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show();
            }
        });

        // set elements to adapter
        checkListView.setAdapter(foldingCellListAdapter);

        // set on click event listener to list view
        checkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                // register in adapter that state for selected cell is toggled
                foldingCellListAdapter.registerToggle(pos);
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
                createItemFor(POS_UNCONFIRMED_LIST).setChecked(true),
                createItemFor(POS_CONFIRMED_LIST),
                new SpaceItem(48),
                createItemFor(POS_IMPORT)));
        slidingRootDrawerAdapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(slidingRootDrawerAdapter);

        slidingRootDrawerAdapter.setSelected(POS_UNCONFIRMED_LIST);
    }

    @Override
    public void onItemSelected(int position) {
        switch (position) {
            case POS_UNCONFIRMED_LIST:
                break;

            case POS_CONFIRMED_LIST:
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
                        setCheckList(data.getData());
                    } else {
                        Toast.makeText(this, "파일 읽기에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    Toast.makeText(this, "파일 읽기에 실패했습니다", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        slidingRootDrawerAdapter.setSelected(POS_UNCONFIRMED_LIST);
    }

    private void setCheckList(Uri pathUri) {
        try {
            new Util.ThreadTask<Uri, ArrayList<Item>>() {
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected ArrayList<Item> doInBackground(Uri arg) {
                    return readExcel(arg);
                }

                @Override
                protected void onPostExecute(ArrayList<Item> result) {
                    if (result != null) {
                        items.clear();
                        items.addAll(result);

                        dbHelper.insertCheckListToDB(result);

                        for (int i = 0; i < result.size(); i++) {
                            registerItemButton(i);
                        }

                        foldingCellListAdapter = new FoldingCellListAdapter(getApplicationContext(), result);
                        checkListView.setAdapter(foldingCellListAdapter);

                        Toast.makeText(getApplicationContext(), "리스트를 성공적으로 불러왔습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "리스트 불러오기에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(pathUri);
        } catch (Exception e) {
            e.printStackTrace();
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

    private ArrayList<Item> readExcel(Uri listUri) {
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        ArrayList<Item> itemList = new ArrayList<>();

        try {
            File file = Util.getImageFile(this, listUri);

            FileInputStream fileInputStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            //Item item = Item.getInstance();
            String reservationDate = "";
            String reservationTime = "";
            String inoculated = "";
            String subject = "";
            String name = "";
            String registrationNumber = "";
            String phoneNumber = "";
            String facilityName = "";

            int totalSheets = workbook.getNumberOfSheets();

            XSSFSheet curSheet;
            XSSFRow curRow;
            XSSFCell curCell;

            Log.d(":: totalSheets", String.valueOf(totalSheets));

            for (int sheetIndex = 0; sheetIndex < totalSheets; sheetIndex++) {
                curSheet = workbook.getSheetAt(sheetIndex);
                Log.d(":: curSheet", String.valueOf(sheetIndex));

                int totalCurRow = curSheet.getPhysicalNumberOfRows();

                for (int rowIndex = 0; rowIndex < totalCurRow; rowIndex++) {
                    if (rowIndex < 2)
                        continue;

                    curRow = curSheet.getRow(rowIndex);

                    DataFormatter dataFormatter = new DataFormatter();
                    String value = "";

                    if (curRow.getCell(0) != null) {
                        int totalCell = curRow.getPhysicalNumberOfCells();
                        Log.d(":: totalCell", String.valueOf(totalCell));

                        for (int cellIndex = 0; cellIndex < totalCell; cellIndex++) {
                            curCell = curRow.getCell(cellIndex);

                            if (curCell != null) {
                                String curValue = dataFormatter.formatCellValue(curCell);
                                Log.d(":: value", curValue);

                                if (!("".equals(curValue))) {
                                    value = curValue;
                                } else {
                                    value = "";
                                }
                            }

                            switch (cellIndex) {
                                case 0: // 2차 예방접종 예약일
                                    //item.setReservationDate(value);
                                    reservationDate = value;
                                    break;

                                case 1: // 예약 시간
                                    //item.setReservationTime(value);
                                    reservationTime = value;
                                    break;

                                case 2: // 접종 완료
                                    //item.setInoculated(value);
                                    inoculated = value;
                                    break;

                                case 3: // 대상자 구분
                                    //item.setSubject(value);
                                    subject = value;
                                    break;

                                case 4: // 이름
                                    //item.setName(value);
                                    name = value;
                                    break;

                                case 5: // 주민등록번호
                                    //item.setRegistrationNumber(value);
                                    registrationNumber = value;
                                    break;

                                case 6: // 전화번호
                                    //item.setPhoneNumber(value);
                                    phoneNumber = value;
                                    break;

                                case 7: // 노인시설명
                                    //item.setFacilityName(value);
                                    facilityName = value;
                                    break;
                            }
                        }
                    }

                    itemList.add(new Item(reservationDate, reservationTime, inoculated,
                            subject, name, registrationNumber, phoneNumber, facilityName));
                }
            }

            if (workbook != null)
                workbook.close();

            if (fileInputStream != null)
                fileInputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return itemList;
    }

    private void registerItemButton(int pos) {
        items.get(pos).setBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "이름: " + items.get(pos).getName(), Toast.LENGTH_SHORT).show();
            }
        });
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