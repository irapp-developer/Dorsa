package dorsa.psb.com.dorsa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import dorsa.psb.com.dorsa.R;
import dorsa.psb.com.dorsa.other.G;

public class Service_StatusBar extends Service {
    private  View pView;
    private WindowManager manager;
    public Service_StatusBar() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       
        return null;
    }

    @Override 
    public void onCreate() {
        Log.d(G.LOG_TAG,"Status bar runn");
        manager= ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        localLayoutParams.height = (int) (50 * getResources().getDisplayMetrics().scaledDensity);
        localLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.format = PixelFormat.TRANSPARENT;



        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                        // this is to enable the notification to recieve touch events
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                        // Draws over status bar
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSPARENT);

        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (25 * getResources().getDisplayMetrics().scaledDensity);
        params.gravity = Gravity.TOP;
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
       /* params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;*/
        
        
        
        
        

        LayoutInflater inflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         pView = inflate.inflate(R.layout.service_statusbar, null);


        manager.addView(pView, params);
        

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pView != null) {
            manager.removeView(pView);
        }
    }
}
