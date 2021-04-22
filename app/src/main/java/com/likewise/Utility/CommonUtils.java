package com.likewise.Utility;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.likewise.Activity.ChatActivity;
import com.likewise.Activity.CreateGameActivity;
import com.likewise.Activity.MainActivity;
import com.likewise.Model.Laguage;
import com.likewise.R;
import com.likewise.SharedPrefrence.SPreferenceKey;
import com.likewise.SharedPrefrence.SharedPreferenceWriter;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.CorneredSort;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CommonUtils extends AppCompatActivity {
    public static CustomProgressBar customProgressBar;
    public static boolean networkConnectionCheck(final Context context) {


        boolean isConnected = isOnline(context);
        if (!isConnected) {
           // showInternetDailog(context);
        }
        return isConnected;


    }

    public static Dialog showInternetDailog(Context context) {
        final Dialog dialog=new Dialog(context,android.R.style.Theme_Black);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_no_internet);
        LottieAnimationView lottieAnimationView=dialog.findViewById(R.id.lottieAnimationView);

        dialog.setCancelable(true);
        dialog.show();

        return dialog;
    }

    public static boolean isOnline(Context context) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile_info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi_info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mobile_info != null) {
                if (mobile_info.isConnectedOrConnecting() || wifi_info.isConnectedOrConnecting()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (wifi_info.isConnectedOrConnecting()) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("" + e);
            return false;
        }
    }


    public static void showSnackBar(Context context,String msg,String type)
    {
        if(context!=null) {
            Snackbar snackbar = null;
            if (context instanceof CreateGameActivity) {
                snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE);
            } else if (context instanceof MainActivity || context instanceof ChatActivity) {
                snackbar = Snackbar.make(((Activity) context).findViewById(R.id.viewSnackbar), msg, Snackbar.LENGTH_LONG);
            } else {
                snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
            }

            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            TextView tv = (TextView) snackBarView.findViewById(R.id.snackbar_text);
            snackBarView.setMinimumHeight(20);
            tv.setTextSize(14);
            tv.setTextColor(ContextCompat.getColor(context, R.color.white));
            if (context instanceof CreateGameActivity) {
                snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.white));
                Snackbar finalSnackbar = snackbar;
                finalSnackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalSnackbar.dismiss();
                    }
                });
            }
            snackbar.show();
        }
    }

    public static String getUserType(String user) {
       String userType="";
        switch (user)
        {
            case "register": userType="2"; break;
            case "facebook": userType="1"; break;
            case "Guest": userType="3"; break;
            case "Instagram": userType="4"; break;
        }

        return userType;

    }

    public static String getGenderType(String gender) {
        String genderType="";
        switch (genderType)
        {
            case "Male": genderType="0"; break;
            case "Female": genderType="1"; break;

        }

        return genderType;

    }

    public static String changeFormatDate(String dob)  {
        String outdate = "";

        if(dob!=null) {

            Date date = null;
            try {
                date = new SimpleDateFormat("dd-MM-yyyy").parse(dob);
                outdate = new SimpleDateFormat("dd MMMM yyyy").format(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return outdate;
    }

    public static File saveDefaultImage(File dir, String fileName, Bitmap bm, Bitmap.CompressFormat format, int quality) {
            File imageFile = new File(dir,fileName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(imageFile);
                bm.compress(format,quality,fos);
                fos.close();
            }
            catch (IOException e) {
                Log.e("app",e.getMessage());
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            return imageFile;

    }

    public static void setVibrate(Context context) {
        boolean vibrate=SharedPreferenceWriter.getInstance(context).getBoolean(SPreferenceKey.VIBRATE);
        if(vibrate) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }

    public static String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }




    public void setProgressbar(final ProgressBar bar, final TextView textView, final int count)
    {
        bar.setProgress(count);
        textView.setText(""+bar.getProgress());
    }

    public static void showLoadingDialog(Activity activity){
        if(customProgressBar==null)
            customProgressBar = CustomProgressBar.show(activity, true);

        try {
            customProgressBar.setCancelable(false);
            customProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public static void dismissLoadingDialog(){
        try
        {
            if (null != customProgressBar && customProgressBar.isShowing()) {
                customProgressBar.dismiss();
                customProgressBar=null;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public  void getFBKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.likewise",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static boolean getDeviceToken(final Context context)
    {
        final boolean[] ret = {true};
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    ret[0]=true;
                    String auth_token = task.getResult().getToken();
                    Log.e("TOKEN",auth_token);
                    SharedPreferenceWriter.getInstance(context).writeStringValue(SPreferenceKey.TOKEN,auth_token);

                }else
                {
                    ret[0] =false;
                    getDeviceToken(context);
                }


            }
        });

        return ret[0];
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
                new SimpleDateFormat("ddmmyyhhmmss").format(Calendar.getInstance().getTime()), null);
        return Uri.parse(path);
    }

    public static void showAnimation(ConstraintLayout view)
    {
        Animator spruceAnimator = new Spruce
                .SpruceBuilder(view)
                .sortWith(new CorneredSort(/*interObjectDelay=*/10L, /*reversed=*/false, CorneredSort.Corner.TOP_LEFT))
                .animateWith(new Animator[] {DefaultAnimations.shrinkAnimator(view, /*duration=*/300)})
                .start();
    }

   public static String getCurrentTimeStamp()
   {
       Calendar c = Calendar.getInstance();
       return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
   }

    public static Animation makeMeBlink(View view, Drawable drawable) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        anim.setStartOffset(1);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
        view.setBackground(drawable);
        return anim;
    }
    public static Animation makeMeBlink(View view, Drawable drawable,long duration) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(duration);
        anim.setStartOffset(1);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
        view.setBackground(drawable);
        return anim;
    }

    public static void playSound(String type,Context context)
    {
      boolean sound=SharedPreferenceWriter.getInstance(context).getBoolean(SPreferenceKey.SOUND);
      if(sound) {
          MediaPlayer mMediaPlayer = null;
          if (type.equalsIgnoreCase(ParamEnum.DRAWER.theValue())) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.woosh);
          } else if (type.equalsIgnoreCase("partner_found")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.partner_found);
          } else if (type.equalsIgnoreCase("start_game")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.start_game);
          } else if (type.equalsIgnoreCase("card_pass")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.partner_wants_to_pass);
          } else if (type.equalsIgnoreCase("coin")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.coin);
          } else if (type.equalsIgnoreCase("bonus")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.bonus);
          } else if (type.equalsIgnoreCase("streaks")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.streak);
          } else if (type.equalsIgnoreCase("life_remaining")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.life_remaining);
          } else if (type.equalsIgnoreCase("match")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.match);
          } else if (type.equalsIgnoreCase("no_match")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.no_match);
          } else if (type.equalsIgnoreCase("enter_chat")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.enter_chat);
          } else if (type.equalsIgnoreCase("game_over")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.game_over);
          } else if (type.equalsIgnoreCase("reveal_chat")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.reveal_chat);
          } else if (type.equalsIgnoreCase("life_lost")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.life_lost);
          } else if (type.equalsIgnoreCase("earn_coin")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.earn_coin);
          } else if (type.equalsIgnoreCase("notification")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.notification);
          } else if (type.equalsIgnoreCase("press_button")) {
              mMediaPlayer = MediaPlayer.create(context, R.raw.press_button);
          }

          mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
          mMediaPlayer.setLooping(false);
          mMediaPlayer.start();
      }


    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("TAG", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("TAG", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("TAG", "printHashKey()", e);
        }
    }


   public static boolean verifyInstallerId(Context context) {
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return installer != null && validInstallers.contains(installer);
    }

        public static void hideKeyboardFrom(Context context, View view) {
        if(view!=null) {
            if (view.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }







}
