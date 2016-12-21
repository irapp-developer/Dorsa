package ir.dorsa.dorsaworld;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 3/4/16 AD.
 */
public class Activity_Dialog extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(G.LOG_TAG,"Activty Dialog happend");
        
        setContentView(R.layout.activity_dialog);

        final Dialog mDialog = new Dialog(Activity_Dialog.this, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mDialog.setContentView(R.layout.dialog_crash);

        Button sendReport=(Button)mDialog.findViewById(R.id.dialog_crash_btn_send_report);
        Button cancel=(Button)mDialog.findViewById(R.id.dialog_crash_btn_back);


        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent sendIntent = new Intent(
                        Intent.ACTION_SEND);
                String subject = "Your App crashed! Fix it!";
                StringBuilder body = new StringBuilder("Yoddle");
                body.append('\n').append('\n');
                body.append(report).append('\n')
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
                context1.startActivity(sendIntent);*/
                System.exit(2);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(2);
            }
        });

        mDialog.show();
        
        
    }
}
