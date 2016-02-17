package dorsa.psb.com.dorsa.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.Func;
import dorsa.psb.com.dorsa.other.G;

public class Fragment_select_launcher extends Fragment {
    private View pView;
    private TextView desc;
    private Button accept;
    private boolean isFirst=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        G.Intro.isFromApp=true;
        pView = inflater.inflate(R.layout.fragment_select_launcher, container, false);

        desc = (TextView) pView.findViewById(R.id.frg_select_launcher_desc);
        accept = (Button) pView.findViewById(R.id.frg_select_launcher_accept);

        final String defaultLauncher = Func.getDefaultLauncher(getActivity());

        if (defaultLauncher.length() > 0 &
                !defaultLauncher.equals("android") &
                !defaultLauncher.equals(getActivity().getPackageName())) {//--- other launcher is default
            desc.setText(R.string.clear_defaults);
            accept.setText("تغییر");
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment_select_launcher.this.startActivityForResult(Func.newAppDetailsIntent(defaultLauncher), 1345);
                }
            });

        } else {//--- show launcher dialog
            desc.setText(R.string.select_launcher);
            accept.setText("تایید");
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent selector = new Intent(Intent.ACTION_MAIN);
                    selector.addCategory(Intent.CATEGORY_HOME);
                    selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Fragment_select_launcher.this.startActivityForResult(selector,1346);
                }
            });
        }

        return pView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(G.LOG_TAG, "On activity result");
        if(isFirst){
            isFirst=false;
        }else {
            final String defaultLauncher = Func.getDefaultLauncher(getActivity());
            if (defaultLauncher.length() > 0 &
                    !defaultLauncher.equals("android") &
                    !defaultLauncher.equals(getActivity().getPackageName())) {//--- other launcher is default
                desc.setText(R.string.clear_defaults);
                accept.setText("تغییر");
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment_select_launcher.this.startActivityForResult(Func.newAppDetailsIntent(defaultLauncher), 1345);
                    }
                });

            } else if (defaultLauncher.length() > 0 &
                    !defaultLauncher.equals("android") &
                    defaultLauncher.equals(getActivity().getPackageName())) {//our launcher is selected
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
               /* accept.setText("پایان");
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                });*/

            } else {//--- show launcher dialog
                desc.setText(R.string.select_launcher);
                accept.setText("تایید");
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent selector = new Intent(Intent.ACTION_MAIN);
                        selector.addCategory(Intent.CATEGORY_HOME);
                        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Fragment_select_launcher.this.startActivityForResult(selector, 1346);
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
    }
}
