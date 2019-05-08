package www.kfstudio.barcode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Random;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class QrCodeScannerActivity extends AppCompatActivity{
    TextToSpeech t1;
    private static final int REQUEST_CAMERA = 1;
    private TextView formatTxt, contentTxt;
    ImageView imageView,tv;
    String text="hello";
    String data_speech="This is made by Kashaf Ahmed";
    TextView wlcm;
    ImageButton text_speech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scaner);
        imageView = findViewById(R.id.qr_code);
        tv=findViewById(R.id.tv);
        wlcm = findViewById(R.id.tv_welcome);
        text_speech=findViewById(R.id.text_speech);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        text_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t1.speak(wlcm.getText(), TextToSpeech.QUEUE_FLUSH, null,TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(QrCodeScannerActivity.this);
                dialog.setContentView(R.layout.custom_image_view);
                dialog.setCancelable(true);
                final Button cancelbtn = dialog.findViewById(R.id.button_cancel);
                ImageView imageview = dialog.findViewById(R.id.image_view);
                final Button download_btn = dialog.findViewById(R.id.button_download);
                download_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bm=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                        saveImageToExternalStorage(bm,text.toLowerCase().trim());
                        Toast.makeText(QrCodeScannerActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                imageview.setImageDrawable(imageView.getDrawable());

                cancelbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }

                });

                dialog.show();
            }
        });
    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(),
                CAMERA ) == PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA,WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA ) {
            if (grantResults.length > 0) {

                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted && writeAccepted) {
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{CAMERA,WRITE_EXTERNAL_STORAGE},
                                                    REQUEST_CAMERA);
                                        }
                                    });
                        }
                    }
                }
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(QrCodeScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {

            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    public void scanNow(View view){
        Intent intent = new Intent(QrCodeScannerActivity.this,CustomScannerActivity.class);
        startActivityForResult(intent,1);
    }
    public void generateNow(View view){
        final Dialog dialog = new Dialog(QrCodeScannerActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(true);
        final Button verifyButton = dialog.findViewById(R.id.btn_verify);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText verificationCode  = dialog.findViewById(R.id.verification_code);
                text = verificationCode.getText().toString();
                wlcm.setText(text);
                if(!text.trim().isEmpty()){
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }else{
                Toast.makeText(QrCodeScannerActivity.this, "Wrong Input", Toast.LENGTH_SHORT).show();
            }}
    });
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data_speech = data.getStringExtra("Result");
            wlcm.setText(data.getStringExtra("Result"));
        }
    }
    private void saveImageToExternalStorage(Bitmap finalBitmap,String title) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/QRcode");
        myDir.mkdirs();
        String fname = title+".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        Toast.makeText(QrCodeScannerActivity.this, "Saved at "+uri, Toast.LENGTH_SHORT).show();

                    }
                });

    }

}