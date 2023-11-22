package com.example.web2apk;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRCodeActivity extends AppCompatActivity
{
    ImageView QR_Code;

    ImageButton close_btn;

    Bitmap QRbitmap;

    MaterialButton download_btn;

    MaterialButton copy_btn;

    String appname;

    String apk_link;

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_activity);

        this.QR_Code = findViewById(R.id.qr_code);
        this.close_btn = findViewById(R.id.close_btn);
        this.download_btn = findViewById(R.id.download_QR);
        this.copy_btn = findViewById(R.id.copy_link_btn);
        this.context = this;

        this.apk_link = getIntent().getStringExtra("APK_LINK");

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = manager.getDefaultDisplay();

        Point point = new Point();
        display.getSize(point);

        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        QRGEncoder qrgEncoder = new QRGEncoder(this.apk_link, null, QRGContents.Type.TEXT, dimen);
        this.QRbitmap = qrgEncoder.getBitmap();

        QR_Code.setImageBitmap(this.QRbitmap);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a ClipData object with the text to copy
                ClipData clip = ClipData.newPlainText("label", apk_link);

                // Set the data to the clipboard
                clipboard.setPrimaryClip(clip);

                // Show a toast indicating that the text has been copied
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        this.download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String downloadDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

                File file = new File(downloadDirPath, appname+".png");

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    QRbitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();

                    MediaScannerConnection.scanFile(
                            QRCodeActivity.this,
                            new String[]{file.toString()},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    // Scan completed
                                }
                            }
                    );

                    Toast.makeText(QRCodeActivity.this, "Image Saved to Downloads", Toast.LENGTH_SHORT).show();
                } catch (Exception e)
                {
                    Toast.makeText(QRCodeActivity.this, "Unable to Save the file, Check Your Storage Permissions"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
