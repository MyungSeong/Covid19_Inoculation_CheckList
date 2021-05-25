package ko.kr.kms.covid19_inoculation_checklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ramotion.foldingcell.FoldingCell;

import java.util.HashSet;
import java.util.List;

public class FoldingCellListAdapter extends ArrayAdapter<Item> {
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultBtnClickListener;

    public FoldingCellListAdapter(Context context, List<Item> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get item for selected view
        Item item = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;

        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);
            // binding view parts to view holder
            viewHolder.title_name = cell.findViewById(R.id.title_name);
            viewHolder.title_reservationDate = cell.findViewById(R.id.title_date_label);
            viewHolder.title_reservationTime = cell.findViewById(R.id.title_time_label);
            viewHolder.title_registrationNumber = cell.findViewById(R.id.title_registrationNumber);
            viewHolder.title_facilityName = cell.findViewById(R.id.title_facilityName);

            viewHolder.reservationDate = cell.findViewById(R.id.content_reservationDate);
            viewHolder.reservationTime = cell.findViewById(R.id.content_reservationTime);
            viewHolder.inoculated = cell.findViewById(R.id.content_inoculated);
            viewHolder.subject = cell.findViewById(R.id.content_subject);
            viewHolder.name = cell.findViewById(R.id.content_name_view);
            viewHolder.registrationNumber = cell.findViewById(R.id.content_reservationNumber);
            viewHolder.phoneNumber = cell.findViewById(R.id.content_phoneNumber);
            viewHolder.facilityName = cell.findViewById(R.id.content_facilityName);
            viewHolder.contentBtn = cell.findViewById(R.id.content_btn);

            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (null == item)
            return cell;

        // bind data from selected element to view through view holder
        viewHolder.title_name.setText(item.getName());
        viewHolder.title_reservationDate.setText(item.getReservationDate());
        viewHolder.title_reservationTime.setText(item.getReservationTime());
        viewHolder.title_registrationNumber.setText(item.getRegistrationNumber());
        viewHolder.title_facilityName.setText(item.getFacilityName());

        viewHolder.reservationDate.setText(item.getReservationDate());
        viewHolder.reservationTime.setText(item.getReservationTime());
        viewHolder.inoculated.setText(item.getInoculated());
        viewHolder.subject.setText(item.getSubject());
        viewHolder.name.setText(item.getName());
        viewHolder.registrationNumber.setText(item.getRegistrationNumber());
        viewHolder.phoneNumber.setText(item.getPhoneNumber());
        viewHolder.facilityName.setText(item.getFacilityName());

        // set custom btn handler for list item from that item
        if (item.getBtnClickListener() != null) {
            viewHolder.contentBtn.setOnClickListener(item.getBtnClickListener());
        } else {
            // (optionally) add "default" handler if no handler found in item
            viewHolder.contentBtn.setOnClickListener(defaultBtnClickListener);
        }

        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultBtnClickListener() {
        return defaultBtnClickListener;
    }

    public void setDefaultBtnClickListener(View.OnClickListener defaultBtnClickListener) {
        this.defaultBtnClickListener = defaultBtnClickListener;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView title_name;
        TextView title_reservationDate;
        TextView title_reservationTime;
        TextView title_registrationNumber;
        TextView title_facilityName;

        TextView reservationDate;
        TextView reservationTime;
        TextView inoculated;
        TextView subject;
        TextView name;
        TextView registrationNumber;
        TextView phoneNumber;
        TextView facilityName;

        TextView contentBtn;
    }
}
