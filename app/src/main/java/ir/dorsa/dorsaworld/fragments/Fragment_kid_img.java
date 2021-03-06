package ir.dorsa.dorsaworld.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ir.dorsa.dorsaworld.R;
import ir.dorsa.dorsaworld.other.Func;
import ir.dorsa.dorsaworld.other.G;

/**
 * Created by mehdi on 1/21/16 AD.
 */
public class Fragment_kid_img extends Fragment {

    private View pView;
    private int CAMERA_REQUEST_USER_IMAGE = 1678;
    private int GALLERY_REQUEST_USER_IMAGE = 9833;
    private int CROP_PIC_REQUEST_CODE = 245;
    private int kidImageWidth = 0;

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        pView = inflater.inflate(R.layout.fragment_child_image, container, false);
        G.addChild.avatarBitmap=null;

        G.kid_details.MaleRel = (RelativeLayout) pView.findViewById(R.id.frg_kid_img_rel_boys);
        G.kid_details.FMaleRel = (RelativeLayout) pView.findViewById(R.id.frg_kid_img_rel_girls);

        G.kid_details.kidsImg = (ImageView) pView.findViewById(R.id.frg_kid_img_selected);

        G.kid_details.boysIcons = new ImageView[6];
        G.kid_details.girlsIcons = new ImageView[6];


        G.kid_details.boys_stroke[0] = (ImageView) pView.findViewById(R.id.frg_kid_img_gallery_storke);
        G.kid_details.boys_stroke[1] = (ImageView) pView.findViewById(R.id.frg_kid_img_camera_storke);
        G.kid_details.boys_stroke[2] = (ImageView) pView.findViewById(R.id.frg_kid_img_boy_1_stroke);
        G.kid_details.boys_stroke[3] = (ImageView) pView.findViewById(R.id.frg_kid_img_boy_2_stroke);
        G.kid_details.boys_stroke[4] = (ImageView) pView.findViewById(R.id.frg_kid_img_boy_3_stroke);
        G.kid_details.boys_stroke[5] = (ImageView) pView.findViewById(R.id.frg_kid_img_boy_4_stroke);


        G.kid_details.girls_stroke[0] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_gallery_storke);
        G.kid_details.girls_stroke[1] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_camera_storke);
        G.kid_details.girls_stroke[2] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_1_stroke);
        G.kid_details.girls_stroke[3] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_2_stroke);
        G.kid_details.girls_stroke[4] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_3_stroke);
        G.kid_details.girls_stroke[5] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_4_stroke);


        G.kid_details.boysIcons[0] = (ImageView) pView.findViewById(R.id.frg_kid_img_gallery);
        G.kid_details.boysIcons[1] = (ImageView) pView.findViewById(R.id.frg_kid_img_camera);
        G.kid_details.boysIcons[2] = (ImageView) pView.findViewById(R.id.frg_kid_img_boy_1);
        G.kid_details.boysIcons[3] = (ImageView) pView.findViewById(R.id.frg_kid_img_boy_2);
        G.kid_details.boysIcons[4] = (ImageView) pView.findViewById(R.id.frg_kid_img_boy_3);
        G.kid_details.boysIcons[5] = (ImageView) pView.findViewById(R.id.frg_kid_img_boy_4);


        G.kid_details.girlsIcons[0] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_gallery);
        G.kid_details.girlsIcons[1] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_camera);
        G.kid_details.girlsIcons[2] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_1);
        G.kid_details.girlsIcons[3] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_2);
        G.kid_details.girlsIcons[4] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_3);
        G.kid_details.girlsIcons[5] = (ImageView) pView.findViewById(R.id.frg_kid_img_girl_4);


        for (int i = 0; i < G.kid_details.boysIcons.length; i++) {
            G.kid_details.boysIcons[i].setOnClickListener(new AvatarClick("B_" + i));
        }

        for (int i = 0; i < G.kid_details.girlsIcons.length; i++) {
            G.kid_details.girlsIcons[i].setOnClickListener(new AvatarClick("G_" + i));
        }

        if (G.kid_details.sexMode == 0) {//boy selected
            G.kid_details.MaleRel.setVisibility(View.VISIBLE);
            G.kid_details.FMaleRel.setVisibility(View.GONE);
            G.kid_details.kidsImg.setImageResource(R.drawable.icon_boy);

        } else {//girl selected
            G.kid_details.MaleRel.setVisibility(View.GONE);
            G.kid_details.FMaleRel.setVisibility(View.VISIBLE);
            G.kid_details.kidsImg.setImageResource(R.drawable.icon_girl);
        }

        TextView BTN_Prev = (TextView) pView.findViewById(R.id.frg_kid_prev);
        TextView BTN_submit = (TextView) pView.findViewById(R.id.frg_kid_ok);

        BTN_Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.mViewPager.setCurrentItem(0);
            }
        });

        BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.mViewPager.setCurrentItem(2);
            }
        });

        G.kid_details.kidsImg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                kidImageWidth = G.kid_details.kidsImg.getMeasuredWidth();
                G.kid_details.kidsImg.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        if (G.addChild.isEditedMode) {
            setDefaultValues();
        }

        return pView;
    }

    class AvatarClick implements View.OnClickListener {
        private String mode;

        public AvatarClick(String mode) {
            this.mode = mode;
        }


        @Override
        public void onClick(View v) {
            if (!mode.contains("0") || !mode.contains("1")) {
                invisibleAllStrokes();// if it was not camera or gallery
            }
            //--------- loock for girls click -----
            if (mode.equals("G_0")) {//select from gallery
//                Fragment_kid_img.this.startActivityForResult(Intent.createChooser(
//                        new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Choose an image"), GALLERY_REQUEST_USER_IMAGE);
                doCrop(null, 1);
            } else if (mode.equals("G_1")) {//select from camera

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                File file = new File(G.dir + "/capture.PNG");
                if (!file.exists()) {
                    try {
                        Log.d(G.LOG_TAG, "file not exists:" + file.createNewFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                file.deleteOnExit();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                Fragment_kid_img.this.startActivityForResult(cameraIntent, CAMERA_REQUEST_USER_IMAGE);

            } else if (mode.equals("G_2")) {
                G.kid_details.kidsImg.setImageResource(R.drawable.icon_girl);
                G.kid_details.girls_stroke[2].setVisibility(View.VISIBLE);
            } else if (mode.equals("G_3")) {
                G.kid_details.kidsImg.setImageResource(R.drawable.icon_girl_1);
                G.kid_details.girls_stroke[3].setVisibility(View.VISIBLE);
            } else if (mode.equals("G_4")) {
                G.kid_details.kidsImg.setImageResource(R.drawable.icon_girl_2);
                G.kid_details.girls_stroke[4].setVisibility(View.VISIBLE);
            } else if (mode.equals("G_5")) {
                G.kid_details.kidsImg.setImageResource(R.drawable.icon_girl_3);
                G.kid_details.girls_stroke[5].setVisibility(View.VISIBLE);
            }
            //--------- loock for boys click -----
            if (mode.equals("B_0")) {//select from gallery
//                Fragment_kid_img.this.startActivityForResult(Intent.createChooser(
//                        new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Choose an image"), GALLERY_REQUEST_USER_IMAGE);
                doCrop(null, 1);
            } else if (mode.equals("B_1")) {//select from camera
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                File file = new File(G.dir + "/capture.PNG");
                if (!file.exists()) {
                    try {
                        Log.d(G.LOG_TAG, "file not exists:" + file.createNewFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                file.deleteOnExit();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                Fragment_kid_img.this.startActivityForResult(cameraIntent, CAMERA_REQUEST_USER_IMAGE);
            } else if (mode.equals("B_2")) {
                G.kid_details.kidsImg.setImageResource(R.drawable.icon_boy);
                G.kid_details.boys_stroke[2].setVisibility(View.VISIBLE);
            } else if (mode.equals("B_3")) {
                G.kid_details.kidsImg.setImageResource(R.drawable.icon_boy_1);
                G.kid_details.boys_stroke[3].setVisibility(View.VISIBLE);
            } else if (mode.equals("B_4")) {
                G.kid_details.kidsImg.setImageResource(R.drawable.icon_boy_2);
                G.kid_details.boys_stroke[4].setVisibility(View.VISIBLE);
            } else if (mode.equals("B_5")) {
                G.kid_details.kidsImg.setImageResource(R.drawable.icon_boy_3);
                G.kid_details.boys_stroke[5].setVisibility(View.VISIBLE);
            }

            if (!mode.contains("0") || !mode.contains("1")) {
                G.addChild.avatarBitmap = ((BitmapDrawable) G.kid_details.kidsImg.getDrawable()).getBitmap();
            }

        }
    }

    public static void invisibleAllStrokes() {
        for (ImageView img : G.kid_details.girls_stroke) {
            img.setVisibility(View.INVISIBLE);
        }
        for (ImageView img : G.kid_details.boys_stroke) {
            img.setVisibility(View.INVISIBLE);
        }
    }

    private void setDefaultValues() {
        try {
            G.addChild.avatarBitmap = Func.getImage("Avatar_" + G.selectedKid.joKid.getString("ID"));
            G.kid_details.kidsImg.setImageBitmap(G.addChild.avatarBitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == GALLERY_REQUEST_USER_IMAGE && data.getData() != null) {
                Uri uri = data.getData();
                doCrop(uri, 1);
            } else if (requestCode == CROP_PIC_REQUEST_CODE && data.getExtras()!=null) {
                if(data.getExtras().get("data")!=null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    G.addChild.avatarBitmap = Func.getRoundedCroppedBitmap(bitmap, kidImageWidth);
                    G.kid_details.kidsImg.setImageBitmap(G.addChild.avatarBitmap);
                    File file = new File(G.dir + "/capture.PNG");
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_USER_IMAGE) {
            Log.d(G.LOG_TAG, "file Activity result");
            File file = new File(G.dir + "/capture.PNG");
            if (file.exists()) {
                Log.d(G.LOG_TAG, "file exist");
                doCrop(Uri.fromFile(file), 0);
            }
        }
    }

    private void doCrop(Uri picUri, int mode) {
        try {
            File file = new File(G.dir + "/capture.PNG");
            if (!file.exists()) {
                try {
                    Log.d(G.LOG_TAG, "file not exists:" + file.createNewFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file.deleteOnExit();
            Intent cropIntent;
            if (mode == 0) {//0---->Camera
                cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(picUri, "image/*");
            } else {//----->Gallery
                cropIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }

            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, file.toURI());
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
            // display an error message
//            String errorMessage = "Whoops - your device doesn't support the crop action!";
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
