package ir.dorsa.dorsaworld.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Fragment_viewpager_kidApps extends Fragment {
    private View pView;
    private TextView desc;
    private Button accept;
    private boolean isFirst=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       /* int from = getArguments().getInt("from",0);
        int to = getArguments().getInt("to",0);
        pView = inflater.inflate(R.layout.fragment_select_kid_apps, container, false);
        RecyclerView mainList=(RecyclerView)pView.findViewById(R.id.frg_kid_apps_recycleview);
        mainList.setAdapter(new Adapter_Launcher_Fragments_Apps(getActivity(),from,to));
        StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mainList.setLayoutManager(mStaggeredLayoutManager);*/
        return pView;
    }

    @Override
    public void onResume() {
        super.onResume();
       
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
    }
}
