package ir.dorsa.dorsaworld.other;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;
import java.util.Locale;

/**
 * Created by mehdi on 3/4/16 AD.
 */
public class UnCaughtException implements Thread.UncaughtExceptionHandler {


    private Context context;
    private static Context context1;

    public UnCaughtException(Context ctx) {
        context = ctx;
        context1 = ctx;
    }

    private StatFs getStatFs() {
        File path = Environment.getDataDirectory();
        return new StatFs(path.getPath());
    }

    private long getAvailableInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    private long getTotalInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    private void addInformation(StringBuilder message) {
        message.append("Locale: ").append(Locale.getDefault()).append('\n');
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi;
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            message.append("Version: ").append(pi.versionName).append('\n');
            message.append("Package: ").append(pi.packageName).append('\n');
        } catch (Exception e) {
            Log.e("CustomExceptionHandler", "Error", e);
            message.append("Could not get Version information for ").append(
                    context.getPackageName());
        }
        message.append("Phone Model: ").append(android.os.Build.MODEL)
                .append('\n');
        message.append("Android Version: ")
                .append(android.os.Build.VERSION.RELEASE).append('\n');
        message.append("Board: ").append(android.os.Build.BOARD).append('\n');
        message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
        message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
        message.append("Host: ").append(android.os.Build.HOST).append('\n');
        message.append("ID: ").append(android.os.Build.ID).append('\n');
        message.append("Model: ").append(android.os.Build.MODEL).append('\n');
        message.append("Product: ").append(android.os.Build.PRODUCT)
                .append('\n');
        message.append("Type: ").append(android.os.Build.TYPE).append('\n');
        StatFs stat = getStatFs();
        message.append("Total Internal memory: ")
                .append(getTotalInternalMemorySize(stat)).append('\n');
        message.append("Available Internal memory: ")
                .append(getAvailableInternalMemorySize(stat)).append('\n');
    }
    public boolean isUIThread(){
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
private Thread thread;
    public void uncaughtException(Thread t, final Throwable e) {
      
        Log.d(G.LOG_TAG,"Error happens");
      /*  
      final Thread d=  new Thread(new Runnable() {
            @Override
            public void run() {
                   
            }
        });
        
          thread=new Thread() {
            @Override
            public void run() {
//                Looper.prepare();
                final StringBuilder report = new StringBuilder();
                Date curDate = new Date();
                report.append("Error Report collected on : ")
                        .append(curDate.toString()).append('\n').append('\n');
                report.append("Informations :").append('\n');
                addInformation(report);
                report.append('\n').append('\n');
                report.append("Stack:\n");
                final Writer result = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(result);
                e.printStackTrace(printWriter);
                report.append(result.toString());
                printWriter.close();
                report.append('\n');
                report.append("**** End of current Report ***");
                Log.e(UnCaughtException.class.getName(),
                        "Error while sendErrorMail" + report);
                
                
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_crash);

                Button sendReport=(Button)mDialog.findViewById(R.id.dialog_crash_btn_send_report);        
                Button cancel=(Button)mDialog.findViewById(R.id.dialog_crash_btn_back);
                
                
                sendReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent(
                                Intent.ACTION_SEND);
                        String subject = "Your App crashed! Fix it!";
                        StringBuilder body = new StringBuilder("Yoddle");
                        body.append('\n').append('\n');
                        body.append(report).append('\n')
                                .append('\n');
                        // sendIntent.setType("text/plain");
                        sendIntent.setType("message/rfc822");
                        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{"coderzheaven@gmail.com"});
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                body.toString());
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                                subject);
                        sendIntent.setType("message/rfc822");
                        context1.startActivity(sendIntent);
                        
                       
                        
                        
                        android.os.Process.killProcess(android.os.Process.myPid());
//                        Process.killProcess(Process.myPid());

                           
                        System.exit(0);
                    }
                });
                
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        android.os.Process.killProcess(android.os.Process.myPid());
//                        Process.killProcess(Process.myPid());
                        System.exit(0);
                    }
                });
                
        mDialog.show();
//                Looper.loop();
            }
        };
        thread.start();
        *//*if(Looper.getMainLooper().getThread() == Thread.currentThread()){
            thread.start();    
        }else{
            Looper.getMainLooper().getThread().stop();
            System.exit(0);
        }*//*
        */
        
    }
    
    
   

    /**
     * This method for call alert dialog when application crashed!
     */
    public void sendErrorMail(final StringBuilder errorContent) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                builder.setTitle("Sorry...!");
                builder.create();
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                System.exit(0);
                            }
                        });
                builder.setPositiveButton("Report",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent sendIntent = new Intent(
                                        Intent.ACTION_SEND);
                                String subject = "Your App crashed! Fix it!";
                                StringBuilder body = new StringBuilder("Yoddle");
                                body.append('\n').append('\n');
                                body.append(errorContent).append('\n')
                                        .append('\n');
                                // sendIntent.setType("text/plain");
                                sendIntent.setType("message/rfc822");
                                sendIntent.putExtra(Intent.EXTRA_EMAIL,
                                        new String[] { "coderzheaven@gmail.com" });
                                sendIntent.putExtra(Intent.EXTRA_TEXT,
                                        body.toString());
                                sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                                        subject);
                                sendIntent.setType("message/rfc822");
                                context1.startActivity(sendIntent);
                                System.exit(0);
                            }
                        });
                builder.setMessage("Oops,Your application has crashed");
                builder.show();
                Looper.loop();
            }
        }.start();
    }
}
