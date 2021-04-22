package com.likewise.Utility;

import android.util.Log;


import com.likewise.Model.SocketModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ServerTimeCalculator {

    public static String getTimeAgo(String dt) {
        String time = "";
        String getDate = dt;
        String server_format = getDate;    //server comes format ?
        String server_format1 = "2019-04-04T13:27:36.591Z";    //server comes format ?
        String myFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                Date date = sdf.parse(server_format);
                String your_format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
                String[] splitted = your_format.split(" ");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date endDate = dateFormat.parse(your_format);
                Date startDate = Calendar.getInstance().getTime();
                long differenceDate = startDate.getTime() - endDate.getTime();
                String[] completeDate = splitted[0].split("-");
                String date1 = completeDate[0];
                String month = completeDate[1];
                String year = completeDate[2];
                int days_in_months = new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date1)).getActualMaximum(Calendar.DAY_OF_MONTH);


                long secounds = 1000;    // 1 secound
                long min = 60 * secounds;  // 1 min
                long hour = 3600000;      // 1 hour
                long day = 86400000;      // 1 days

                differenceDate=differenceDate-65000;
                long monthdifference = differenceDate / (days_in_months * day);
                long daysDifference = differenceDate / day;
                long hourdifference = differenceDate / hour;
                long mindifference = differenceDate / min;
                long secoundsDiffer = differenceDate / secounds;

                if (monthdifference > 0) time =  monthdifference==1?""+monthdifference+" month ago":monthdifference+ " months ago";
                else if (daysDifference > 0) time = daysDifference==1?""+daysDifference+" day ago":daysDifference+ " days ago";
                else if (hourdifference > 0) time = hourdifference==1?""+hourdifference+" hour ago":hourdifference+ " hours ago";
                else if (mindifference > 0) time = mindifference==1?""+mindifference+" min ago":mindifference+ " mins ago";
                else if (secoundsDiffer > 0) time = secoundsDiffer + " secs ago";
                else time="now";

            } catch (ParseException e) {
                e.printStackTrace();
            }

        return time;
    }
    public static long getTime(String dt) {
        long time = 0;
        String getDate = dt;
        String server_format = getDate;    //server comes format ?
        String server_format1 = "2019-04-04T13:27:36.591Z";    //server comes format ?
        String myFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                Date date = sdf.parse(server_format);
                String your_format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
                String[] splitted = your_format.split(" ");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                Date endDate = dateFormat.parse(your_format);
                Date startDate = Calendar.getInstance().getTime();
                long differenceDate = startDate.getTime() - endDate.getTime();
                Log.e("difference", ""+differenceDate);
                long secounds = 1000;    // 1 secound
                long secoundsDiffer = (differenceDate / 1)-65000;
                Log.e("difference", ""+secoundsDiffer);
                String dat="";
                 if(secoundsDiffer<31000)
                 {
                    time= secoundsDiffer;
                 }else
                 {
                     time=0;
                 }



            } catch (ParseException e) {
                e.printStackTrace();
            }
            return time;

    }

    public static long getDateDifference(String dt)
    {
        long differenceDate = 0;
        String getDate = dt;
        String server_format = getDate;    //server comes format ?
        String server_format1 = "2019-04-04T13:27:36.591Z";    //server comes format ?
        String myFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = sdf.parse(server_format);
            String your_format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(date);
            String[] splitted = your_format.split(" ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date endDate = dateFormat.parse(your_format);
            Date startDate = Calendar.getInstance().getTime();
             differenceDate = startDate.getTime() - endDate.getTime();
    }   catch (Exception e)
        {
            e.printStackTrace();
        }
        return differenceDate;
    }
}
