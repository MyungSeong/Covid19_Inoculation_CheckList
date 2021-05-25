package ko.kr.kms.covid19_inoculation_checklist;

import android.view.View;

import java.util.ArrayList;

public class Item {

    /**
     * reservationDate: 2차접종 예약일
     * reservationTime: 예약 시간
     * inoculated: 접종완료
     * subject: 대상자 구분
     * name: 성명
     * reservationNumber: 주민등록번호
     * phoneNumber: 전화번호
     * facilityName: 노인시설명
     */

    private int selectedMenuID;

    private String reservationDate;
    private String reservationTime; // = new SimpleDateFormat("yyyy-MM-dd a HH", Locale.KOREA);
    private String inoculated;
    private String subject;
    private String name;
    private String registrationNumber;
    private String phoneNumber;
    private String facilityName;

    private View.OnClickListener btnClickListener;

    private Item() {
    }

    public Item(String reservationDate, String reservationTime, String inoculated, String subject,
                String name, String registrationNumber, String phoneNumber, String facilityName) {
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.inoculated = inoculated;
        this.subject = subject;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.phoneNumber = phoneNumber;
        this.facilityName = facilityName;
    }

    private static class LazyHolder {
        public static final Item instance = new Item();
    }

    public static Item getInstance() {
        return LazyHolder.instance;
    }

    public int getSelectedMenuID() { return selectedMenuID; }

    public void setSelectedMenuID(int selectedMenuID) {
        this.selectedMenuID = selectedMenuID;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getInoculated() {
        return inoculated;
    }

    public void setInoculated(String inoculated) {
        this.inoculated = inoculated;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public View.OnClickListener getBtnClickListener() {
        return btnClickListener;
    }

    public void setBtnClickListener(View.OnClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (requestsCount != item.requestsCount) return false;
        if (price != null ? !price.equals(item.price) : item.price != null) return false;
        if (pledgePrice != null ? !pledgePrice.equals(item.pledgePrice) : item.pledgePrice != null)
            return false;
        if (fromAddress != null ? !fromAddress.equals(item.fromAddress) : item.fromAddress != null)
            return false;
        if (toAddress != null ? !toAddress.equals(item.toAddress) : item.toAddress != null)
            return false;
        if (date != null ? !date.equals(item.date) : item.date != null) return false;
        return !(time != null ? !time.equals(item.time) : item.time != null);

    }*/

    /*@Override
    public int hashCode() {
        int result = price != null ? price.hashCode() : 0;
        result = 31 * result + (pledgePrice != null ? pledgePrice.hashCode() : 0);
        result = 31 * result + (fromAddress != null ? fromAddress.hashCode() : 0);
        result = 31 * result + (toAddress != null ? toAddress.hashCode() : 0);
        result = 31 * result + requestsCount;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }*/

    /**
     * @return List of elements prepared for tests
     */
    public static ArrayList<Item> getTestingList() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("2021-05-23", "오전 10시", "1차", "노인시설", "홍길동", "960504-1******", "010-1234-5678", "실버타운"));
        items.add(new Item("2021-05-24", "오후 1시", "1차", "노인시설", "김첨지", "970813-2******", "010-2345-6789", "요양원"));

        return items;
    }
}
