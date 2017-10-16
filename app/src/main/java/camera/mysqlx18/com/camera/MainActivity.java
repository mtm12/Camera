package camera.mysqlx18.com.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import static camera.mysqlx18.com.camera.R.id.cameraView;

public class MainActivity extends AppCompatActivity {

    SurfaceView cameraView;
    BarcodeDetector barcode;
    CameraSource cameraSource;
    SurfaceHolder holder;
    private Boolean doOnce = true;
    private TextView barcodeTextView;
    public static String mainProductTitle = "";
    public static final int REQUEST_CODE = 1;
    public static final int PERMISSION_REQUEST = 200;
    public static double screenInches;
    public static int widthPx;
    public static int heightPx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthPx =dm.widthPixels;
        heightPx =dm.heightPixels;
        double wi=(double)widthPx/(double)dm.xdpi;
        double hi =(double)heightPx/(double)dm.ydpi;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        screenInches = Math.sqrt(x+y);
        Log.v("dimens", Integer.toString(widthPx) + " " + Integer.toString(heightPx) + " " + Double.toString(screenInches));

        Button button = (Button)findViewById(R.id.nextBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        startCamera();


    }

    protected void startCamera(){
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();
        barcode = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);

        }

        if (!barcode.isOperational()) {
            Toast.makeText(getApplicationContext(), "Sorry could not setup detector!", Toast.LENGTH_LONG).show();
            this.finish();
        }
        cameraSource = new CameraSource.Builder(this, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1687, 900)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraView.getHolder());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    android.view.ViewGroup.LayoutParams lp = cameraView.getLayoutParams();
                    lp.width = (int) (widthPx * 0.6); // required width
                    lp.height = (int) (heightPx * 0.6); // required height
                    cameraView.setLayoutParams(lp);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                //Log.d("Detection", "Detector1");
                if (barcodes.size() > 0) {
                    if (doOnce) {
                        Barcode thisCode = barcodes.valueAt(0);


                        Log.v("BarcodeData2", thisCode.rawValue);
                        //Log.v("BarcodeData2", barcode.displayValue);
                        //barcodeTextView.setText(thisCode.rawValue);
                        //Intent intent = new Intent();
                        //Toast.makeText(getApplicationContext(), thisCode.rawValue, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
//                        intent.putExtra("barcode", thisCode.rawValue);
//                        //intent.putExtra("barcode", "data");
                        startActivityForResult(intent, REQUEST_CODE);
                        //setResult(RESULT_OK, intent);
                        //finish();
                        doOnce = false;
                    }
                }
            }

        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        doOnce = true;
        startCamera();
        Toast.makeText(getApplicationContext(), "OnRestart Method call", Toast.LENGTH_LONG).show();
    }
}
