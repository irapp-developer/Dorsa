package ir.dorsa.dorsaworld.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

public class Fragment_set_task extends Fragment {
    private View pView;
    private TextView desc;
    private Button accept;
    private boolean isFirst=true;
    
    private static final int REQUEST_ENABLE_USAGE_STATE=8573;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        G.Intro.isFromApp=true;
        pView = inflater.inflate(R.layout.fragment_set_task_access, container, false);

        desc = (TextView) pView.findViewById(R.id.frg_set_task_desc);
        accept = (Button) pView.findViewById(R.id.frg_set_task_accept);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_set_task.this.startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), REQUEST_ENABLE_USAGE_STATE);
            }
        });
        return pView;
    }

   

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_USAGE_STATE) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Func.isUsageStateEnable(getActivity())) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        }
    }
}
