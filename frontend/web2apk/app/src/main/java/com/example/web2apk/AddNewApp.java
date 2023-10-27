package com.example.web2apk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.pavlospt.roundedletterview.RoundedLetterView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddNewApp extends AppCompatActivity
{
    ImageButton back_btn;

    private TextInputLayout appname_input;
    private TextInputLayout applink_input;
    RoundedLetterView roundedLetterView;

    MaterialButton colorPicker;

    private AlertDialog progressDialog;

    private MaterialButton pickimg_btn;

    private boolean hasSelectedImage = false;

    View color_show;

    TextView color_text;

    private int REQUEST_CODE_PICK_IMAGE = 1234;
    private int STORAGE_PERMISSION_CODE = 4321;

    private MaterialButton create_app_btn;

    private Bitmap globalBitmap;

    private Bitmap defaultCircularBitmap;

    CheckBox camera;
    CheckBox location;
    CheckBox file_sys;
    CheckBox bluetooth;

    CheckBox webcache;

    Context context;

    public void showspinner()
    {
        progressDialog.show();
    }

    public void hidespinner()
    {
        progressDialog.dismiss();
    }


    public Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);
        return returnedBitmap;
    }

    public Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = Color.RED; // Any color you want for the border
        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void handleCropResult(Intent intent) {
        final Uri resultUri = UCrop.getOutput(intent);
        if (resultUri != null) {
            // Now you can use the cropped image URI as needed.
            // For example, set it to an ImageView.
            ImageView imageView = findViewById(R.id.imgview);
            imageView.setImageURI(resultUri);
        } else {
            Toast.makeText(this, "Cropping failed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();

                // Provide a destination URI for the cropped image
                Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped"));

                UCrop.Options options = new UCrop.Options();
                options.setCircleDimmedLayer(true); // This will set the dimmed layer to be circular

                UCrop.of(selectedImageUri, destinationUri)
                        .withOptions(options)
                        .withAspectRatio(1,1)
                        .start(this);
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            ImageView imageView = findViewById(R.id.imgview);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                Bitmap circularBitmap = getCircularBitmap(bitmap);
                imageView.setImageBitmap(circularBitmap); // Assuming you have an ImageView to display the result
                TextView defaulttext = findViewById(R.id.default_text);
                defaulttext.setText("Selected:");
                hasSelectedImage = true;
                this.globalBitmap = circularBitmap;
                imageView.setVisibility(View.VISIBLE);
                roundedLetterView.setVisibility(View.INVISIBLE);
            } catch (IOException e) {
            }
            // Use the cropped image Uri
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            // Handle the cropping error
        }
    }

    private void checkPermissionAndPickImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            pickImageFromGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isHexCode(String hexCode) {
        if (hexCode.length() < 2 || hexCode.charAt(0) != '#') {
            return false;
        }

        for (int i = 1; i < hexCode.length(); i++) {
            if (!Character.isDigit(hexCode.charAt(i)) &&
                    !Character.isAlphabetic(hexCode.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public void showColorPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.colorpicker_dialog, null);

        ColorPickerView colorPickerView = view.findViewById(R.id.colorPickerView);
        EditText hexCodeEditText = view.findViewById(R.id.hexCodeEditText);

        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                String hexCode = envelope.getHexCode();
                hexCodeEditText.setText("#"+hexCode);
                hexCodeEditText.setTextColor(Color.parseColor("#"+hexCode));
            }
        });

        builder.setView(view)
                .setTitle("Select a Color")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the color selection or the OK action here
                        String selectedHex = hexCodeEditText.getText().toString();
                        if(isHexCode(selectedHex)) {
                            color_show.setBackgroundColor(Color.parseColor(selectedHex));
                            color_text.setText(selectedHex);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public boolean validate_input(String appname,String website,String hexCode,String permission,String webCache)
    {
        boolean boole = true;

        if(appname.length() < 3)
        {
            appname_input.setError("AppName should contain more than 3 characters");
            boole = false;
        }
        else{
            appname_input.setError(null);
        }

        try {
            new URL(website).toURI();
            applink_input.setError(null);
        } catch (Exception e) {
            applink_input.setError("Enter a Valid Website Link. Like: http://example.com");
            boole = false;
        }

        return boole;
    }

    public String BitMapToString(Bitmap userImage1)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String Document_img1 = Base64.encodeToString(b, Base64.DEFAULT);
        return Document_img1;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnewapp_layout);

        appname_input = findViewById(R.id.appname_input);

        applink_input = findViewById(R.id.website_link_input);

        roundedLetterView = findViewById(R.id.rounded_profile);

        pickimg_btn = findViewById(R.id.icon_select);

        back_btn = findViewById(R.id.back_btn);

        colorPicker = findViewById(R.id.color_select);

        color_show = findViewById(R.id.color_show);

        color_text = findViewById(R.id.color_hexcode);

        create_app_btn = findViewById(R.id.create_app_btn);

        camera = findViewById(R.id.camera_access);
        location = findViewById(R.id.location_access);
        file_sys = findViewById(R.id.file_access);
        bluetooth = findViewById(R.id.bluetooth_access);
        webcache = findViewById(R.id.cache_check);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.loading_layout);
        builder.setCancelable(false); // prevent users from canceling the dialog
        progressDialog = builder.create();

        this.context  = this;
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        appname_input.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 1)
                {
                    String title = s.toString();
                    roundedLetterView.setTitleText(title.substring(0,1).toUpperCase(Locale.ROOT));
                }
            }
        });

        roundedLetterView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                roundedLetterView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if(roundedLetterView.getWidth() > 0 && roundedLetterView.getHeight() > 0) {
                    Bitmap bitmap = getBitmapFromView(roundedLetterView);
                    defaultCircularBitmap = bitmap;
                }
                else{
                }
            }
        });

        pickimg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });

        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });


        create_app_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appname = appname_input.getEditText().getText().toString();
                String website = applink_input.getEditText().getText().toString();
                String hexCode = color_text.getText().toString();


                String permission = "";

                if(camera.isChecked())
                {
                    permission+="1";
                }
                else{
                    permission+="0";
                }

                if(location.isChecked())
                {
                    permission+="1";
                }
                else{
                    permission+="0";
                }

                if(file_sys.isChecked())
                {
                    permission+="1";
                }
                else {
                    permission+="0";
                }

                if(bluetooth.isChecked())
                {
                    permission+="1";
                }
                else {
                    permission+="0";
                }

                String webCache = "";

                if(webcache.isChecked())
                {
                    webCache+="1";
                }
                else{
                    webCache+="0";
                }

                if(validate_input(appname,website,hexCode,permission,webCache))
                {
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.BASE_URL)).addConverterFactory(GsonConverterFactory.create()).build();

                    RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

                    HashMap<String,String> map = new HashMap<>();

                    map.put("appname",appname);
                    map.put("applink",website);
                    map.put("appcolor",hexCode);
                    map.put("permission",permission);
                    map.put("webCache",webCache);

                    Bitmap bitmapp;

                    if(hasSelectedImage)
                    {
                        bitmapp = globalBitmap;
                    }
                    else{
                        bitmapp = defaultCircularBitmap;
                    }

                    String bitmapbase = BitMapToString(bitmapp);

                    map.put("appicon",bitmapbase);

                    class longthread extends Thread
                    {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showspinner();
                                }
                            });

                            Call<Void> call = retrofitInterface.executeCreate(map);

                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response)
                                {
                                    if(response.code() == 200) {
                                        Toast.makeText(context, "App Created Successfully!!!", Toast.LENGTH_LONG).show();
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hidespinner();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(context,"Unable to Create App.Check Your Internet Connection And Try Again.",Toast.LENGTH_LONG).show();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hidespinner();
                                        }
                                    });
                                }
                            });
                        }
                    }

                    new longthread().start();
                }
            }
        });

    }
}
