package com.iiui.craapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.emrekose.recordbutton.OnRecordListener;
import com.emrekose.recordbutton.RecordButton;
import com.iiui.craapp.BuildConfig;
import com.iiui.craapp.R;
import com.iiui.craapp.model.ReportModelClass;
import com.iiui.craapp.util.UtilClass;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ReportActivity extends AppCompatActivity {

    //Activity request codes
    private  static  final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE=100;
    private  static  final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE=200;
    public static final int MEDIA_TYPE_IMAGE=1;
    public static final int MEDIA_TYPE_VIDEO=2;

    //Directory name to store capture images and videos
    private static final String IMAGE_DIRECTORY_NAME="CRA";

    private Uri fileUri;
    private ImageView imgPreview;
    private VideoView videoPreview;
    private Button btnCapturePicture,btnRecordVideo, btnStartRecording, btnStopRecording, btnPlayRecording, btnPauseRecording;
    boolean PictureTaken = false;

    String mCurrentPhotoPath;   //to be used to view images
    String mCurrentPhotoPathForServer; //to be used to send files

    //Attributes n Variables relating to audio
    String AudioSavePathInDevice=null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName="Audio";
    public static final int RequestPermissionCode=1;
    MediaPlayer mediaPlayer;

    //timer
    private long startTime = 0L;
    private long finalTime = 0L; //to display length of recording
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private Timer timer;
    TextView recordingTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        imgPreview=(ImageView) findViewById(R.id.imgPreview);
        btnCapturePicture=(Button) findViewById(R.id.btnCapturePicture);
        btnRecordVideo=(Button) findViewById(R.id.btnCaptureVideo);

        //buttons for recording
        btnStartRecording = (Button) findViewById(R.id.btnStartRecording);
        btnStopRecording = (Button) findViewById(R.id.btnStopRecording);
        btnPlayRecording = (Button) findViewById(R.id.btnPlayRecording);
        btnPauseRecording = (Button) findViewById(R.id.btnPauseRecording);

        recordingTimer = (TextView) findViewById(R.id.recordingTimer);




        //hiding stop and pause button
        btnStopRecording.setEnabled(false);
        //btnStopRecording.setVisibility(View.INVISIBLE);

        btnPauseRecording.setEnabled(false);
        //btnStopRecording.setVisibility(View.INVISIBLE);

        btnPlayRecording.setEnabled(false);
        //btnStopRecording.setVisibility(View.INVISIBLE);

        /*
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
*/





        //Capture image button click event
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //capture picture
                captureImage();

                Log.e("Capture Picture", "Capture Button Clicked");
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }


        //Check for permissions
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED  &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                )
        {
            Log.e("Permission","Granting Permissions");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        }else
        {
            Log.e("Permission","Permission already granted");
        }


    }

    //This function handles button clicks
    public void buttonClickHandler(View view)
    {
        switch (view.getId())
        {
            case R.id.btnStartRecording:
                startAudioRecording();
                break;

            case R.id.btnStopRecording:
                Toast.makeText(getApplicationContext(),
                        "Recording STOP",
                        Toast.LENGTH_SHORT).show();

                mediaRecorder.stop();

                btnStopRecording.setEnabled(false);
                btnPlayRecording.setEnabled(true);
                btnStartRecording.setEnabled(true);

                if (timer != null) {
                    timer.cancel();
                }

                recordingTimer.setText("Recording Done: " + finalTime);
                Toast.makeText(getApplicationContext(),"Time: " + finalTime,Toast.LENGTH_LONG).show();


                break;

            case R.id.btnPauseRecording:
                Toast.makeText(getApplicationContext(),
                        "Pause Recording",
                        Toast.LENGTH_LONG).show();
                break;

            case R.id.btnPlayRecording:
                Toast.makeText(getApplicationContext(),
                        "Playing",
                        Toast.LENGTH_SHORT).show();

                recordingTimer.setText("Playing Audio");

                btnStopRecording.setEnabled(false);
                btnStartRecording.setEnabled(false);

                mediaPlayer=new MediaPlayer();
                try{
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                }catch (IOException e){
                    e.printStackTrace();
                }

                mediaPlayer.start();

                break;

            default:
                break;
        }

    }

    // Create and handle media recorder functionality
    public  void setMediaRecorderReady(){
        //MediaRecorder class to record audio or video
        mediaRecorder=new MediaRecorder();
        //setting the source output and encoding format and output file.
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioEncodingBitRate(128000);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private void startAudioRecording()
    {
        String timeStamp = new SimpleDateFormat("yy.MM.dd_HH.mm.ss", Locale.getDefault()).format(new Date());
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"CRA");

        Log.i("getOutputMediaFile:mediaStorageDir ", mediaStorageDir.toString());
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Audio", "failed to create audio directory");

            }

            Log.i("Audio Success", "failed to create audio directory");
        }

        //AudioSavePathInDevice= Environment.getExternalStorageDirectory().getAbsolutePath()+"/" + "CRA" +"/"+"Audio_" + timeStamp + ".m4a";
        AudioSavePathInDevice= mediaStorageDir +"/"+"Audio_" + timeStamp + ".wav";
        Log.e("AudioSavePath: ", AudioSavePathInDevice);

        setMediaRecorderReady(); //Creating and initializing media recorder object
        try{
            mediaRecorder.prepare();
            mediaRecorder.start();

            //Start Timer
            startTime = SystemClock.uptimeMillis();
            timer = new Timer();
            MyTimerTask myTimerTask = new MyTimerTask();
            timer.schedule(myTimerTask, 1000, 1000);

            btnStartRecording.setEnabled(false);
            btnStopRecording.setEnabled(true);

            Toast.makeText(getApplicationContext(),
                    "Recording Started",
                    Toast.LENGTH_LONG).show();

        }catch (Exception e){

            e.printStackTrace();
        }

    }



    //Checking device has camera hardware or not
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    // This function asks for permissions if they are not granted already
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                boolean isPerpermissionForAllGranted = false;
                if (grantResults.length > 0 && permissions.length==grantResults.length) {
                    for (int i = 0; i < permissions.length; i++){
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                            isPerpermissionForAllGranted=true;

                            Toast.makeText(getApplicationContext(),"Permission Granted: " + permissions[i], Toast.LENGTH_LONG).show();
                        }else{
                            isPerpermissionForAllGranted=false;
                        }
                    }

                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    isPerpermissionForAllGranted=true;
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                if(isPerpermissionForAllGranted){
                    // Do your work here
                }
                break;

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    //Here we store the file url as it will be null after returning from camera app
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        Log.i("fileUri taken from savedInstance: ",fileUri.toString());
    }

    // Create a file Uri for saving an image or video
    public Uri getOutputMediaFileUri(int type) {

        Uri photoURI = FileProvider.getUriForFile(ReportActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                getOutputMediaFile(type));
        Log.e("URL PATH: ", photoURI.toString());
        return photoURI;

    }




    // Create a File for saving an image or video
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        if(Environment.getExternalStorageState().equals("MEDIA_MOUNTED"))
        {

            Log.e("Storage Info", "Storage Exists");
        }
        else
        {
            Log.e("Storage Info", "Storage Does NOT Exist");
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        Log.i("getOutputMediaFile:mediaStorageDir ", mediaStorageDir.toString());

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CAMERA ACTIVITY", "failed to create directory");
                return null;
            }
        }

        // Create datetime to attach with file name
        //String timeStamp = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss", Locale.getDefault()).format(new Date());
        String timeStamp = new SimpleDateFormat("yy.MM.dd_HH.mm.ss", Locale.getDefault()).format(new Date());

        Log.v("FILE NAME WILL BE:", "ReportImage_" + timeStamp + ".jpg");

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            /*
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "ReportImage_" + timeStamp + ".jpg");
*/

            //for testing with short names
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "r"+ timeStamp +".png");


            mCurrentPhotoPath = "file:" + mediaFile.getAbsolutePath();
            Log.e("Absolute Path:", mCurrentPhotoPath);
            mCurrentPhotoPathForServer = mediaFile.getAbsolutePath();

        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }





    /*
    As we are using android’s inbuilt camera app, launching the camera and taking the picture
     can done with very few lines of code using the power of Intent.

    MediaStore.ACTION_IMAGE_CAPTURE – Requesting camera app to capture image
    MediaStore.EXTRA_OUTPUT – Specifying a path where the image has to be stored

    captureImage() function will launch the camera to snap a picture.
     */

    //Capturing Camera image will launch camera app request image capture
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        Log.i("captureImage:fileUrl",fileUri.getPath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }



    /*As we used inbuilt camera app to capture the picture we won’t get the image in onActivityResult() method.
    In this case data parameter will be always null. We have to use fileUri to get the file path
     and display the image
    onActivityResult() we use CAMERA_CAPTURE_IMAGE_REQUEST_CODE to check whether response
     came from Image Capture activity or Video Capture activity.
    Call previewCapturedImage() in onActivityResult after doing this check.
    */
    //Display image from a path to ImageView
    private void previewCapturedImage() {
        try {
            // hide video preview
            //videoPreview.setVisibility(View.GONE);
            imgPreview.setVisibility(View.VISIBLE);

            // bitmap factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 1;

            Log.i("previewCapturedImage:mCurrentPhotoPath",mCurrentPhotoPath);

            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());

            Log.i("previewCapturedImage:Uri.getPath()",imageUri.getPath());

            final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(),
                    options);

            imgPreview.setImageBitmap(bitmap);

            TextView tvImage = (TextView) findViewById(R.id.tvImage);
            tvImage.setText("Image saved at: " + imageUri.getPath());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    //Receiving activity result method will be called after closing the camera


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
                Toast.makeText(getApplicationContext(),
                        "Image Taken: " + mCurrentPhotoPath, Toast.LENGTH_SHORT)
                        .show();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void submitButtonClick(View view)
    {
        new SendFilesTask().execute();
        Toast.makeText(getApplicationContext(),
                "SEND", Toast.LENGTH_SHORT)
                .show();

    }

    private void showResult(String result) {

        if (result != null) {

            // display a notification to the user with the response information

            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            Log.i("Response: ", result);

        } else {

            Toast.makeText(this, "I got null, something happened!", Toast.LENGTH_LONG).show();


        }

    }

    /*  ***************************************

    All functionality related to Background task is written in this inner class

    *************************************** */

    private class SendFilesTask extends AsyncTask<String, Void, String> {

        private MultiValueMap<String, Object> formData;

        private File[] GetFiles(String DirectoryPath) {
            File f = new File(DirectoryPath);
            f.mkdirs();
            return f.listFiles();
        }

        private ArrayList<String> getFileNames(File[] file){
            ArrayList<String> arrayFiles = new ArrayList<String>();
            if (file.length == 0)
                return null;
            else {
                for (int i=0; i<file.length; i++)
                    arrayFiles.add(file[i].getName());
            }

            return arrayFiles;
        }

        //Function to recieve input from user and store it in object
        private ReportModelClass recieveInputFromUser()
        {
            EditText etReportDesc = (EditText) findViewById(R.id.tvReportDesc);
            EditText etNumOfVictoms = (EditText) findViewById(R.id.tvReportNumOfVictoms);
            EditText etNumOfWitness = (EditText) findViewById(R.id.tvReportNumOfWitness);
            EditText etNumOfSuspects = (EditText) findViewById(R.id.tvReportNumOfSuspect);
            //EditText etNumOfSuspects = (EditText) findViewById(R.id.tvReportNumOfSuspect);



            ReportModelClass reportModelClass = new ReportModelClass();


            reportModelClass.setReportDesc(etReportDesc.getText().toString());

            //if fields are empty don't take input from them
            if(!TextUtils.isEmpty(etNumOfVictoms.getText().toString())) {
                reportModelClass.setNumOfVictoms(etNumOfVictoms.getText().toString());
            }

            if(!TextUtils.isEmpty(etNumOfWitness.getText().toString())) {
                reportModelClass.setNumOfWitness(etNumOfWitness.getText().toString());
            }

            if(!TextUtils.isEmpty(etNumOfVictoms.getText().toString())) {

                reportModelClass.setSuspects(etNumOfSuspects.getText().toString());
            }
            //reportModelClass.setReportDesc(etReportDesc.getText().toString());



            return reportModelClass;
        }



        @Override
        protected void onPreExecute() {

            //1. Get the name of the directory where pics are stored
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

            //onPreExecute:mediaStorageDir: /storage/emulated/0/Pictures/CRA
            Log.i("onPreExecute:mediaStorageDir ", mediaStorageDir.toString());

            //2. Get all files from mediaStorageDir to a list
            File[] fileList = GetFiles(mediaStorageDir.getPath());

            //4. Display all names of files
            //assert fileNames != null;
            for (File file:fileList
                    ) {
                Log.e("FILE:", file.getAbsolutePath());

            }

            //3. Take names of all files using list
            ArrayList<String> fileNames = getFileNames(fileList);

            //4. Display all names of files
            //assert fileNames != null;
            for (String fileName:fileNames
                 ) {
                Log.e("FILE:", fileName);

            }

            //5. Return only one file name, latest

           //Send data
                try {

                    //Creating formData object which will be send to server containing Key Value pairs
                    formData = new LinkedMultiValueMap<String, Object>();


                    //Check if picture not taken attach dummy picture
                   /* if(false) {

                        // /storage/emulated/0/Pictures/CRA/r18.02.20_09.10.30.png
                        formData.add("File", new FileSystemResource(new File("/storage/emulated/0/Pictures/CRA/r18.02.20_09.10.30.png")));

                        Toast.makeText(getApplicationContext(),"Picture NOT Attached", Toast.LENGTH_SHORT).show();
                        Log.e("formData:Picture", "Picture NOT Attached");

                    }
                    else
                    {
*/
                        //Attaching Image with FormData
                        Uri imageUri = Uri.parse(mCurrentPhotoPath);
                        File file = new File(imageUri.getPath());

                        Log.e("Async:Uri.getPath", imageUri.getPath());

                        //Attach pic with form data
                        formData.add("File", new FileSystemResource(file));
                        Toast.makeText(getApplicationContext(),"Picture Attached: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                        Log.e("formData:Picture", "Picture Attached");

  //                  }

                    //Attaching Audio with FormData
                    Uri audioUri = Uri.parse(AudioSavePathInDevice);
                    File fileAudio = new File(audioUri.getPath());

                    Log.e("Async:Uri.audioPath", audioUri.getPath());

                    //Attach pic with form data
                    formData.add("File", new FileSystemResource(fileAudio));
                    Toast.makeText(getApplicationContext(),"Audio Attached: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                    Log.e("formData:Picture", "Audio Attached");



                    String timeStamp = new SimpleDateFormat("yy.MM.dd_HH.mm.ss", Locale.getDefault()).format(new Date());


                    //Taking Input from User
                    ReportModelClass report = recieveInputFromUser();


                    // Sending all fields separately
                    formData.add("ReportDesc", report.getReportDesc());
                    formData.add("NumOfVictoms", report.getNumOfVictoms() );
                    formData.add("NumOfWitness", report.getNumOfWitness());
                    formData.add("NumOfSuspects", report.getSuspects());
                    formData.add("ReportSubmitTime", timeStamp.toString());
                    formData.add("ClientDocs", "ClientDocs");

                }catch(Exception ef)
                {
                    Log.e("Error", ef.toString());
                }




        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // The URL for making the POST request
                //final String url = "http://192.168.56.1:45455/api/Crime/2";
                final String url = getString(R.string.base_url) + "/DocumentUpload/MediaUpload";

                HttpHeaders requestHeaders = new HttpHeaders();
                // Sending multipart/form-data

                requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

                // Populate the MultiValueMap being serialized and headers in an HttpEntity object to use for the request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        formData, requestHeaders);

                // Create a new RestTemplate instance
                RestTemplate restTemplate = new RestTemplate(true);
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());

                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
                //restTemplate.getMessageConverters().add(new Conver);


                // Make the network request, posting the message, expecting a String in response from the server
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        String.class);

                Log.i("Response from Server", response.toString());

                String headerResponse = response.getHeaders().get("DocsUrl").toString();

                // Return the response body to display to the user
                return response.getStatusCode().getReasonPhrase();
            } catch (Exception e) {
                Log.e("Error with background", e.toString());
            }


            return null;
        }



        @Override
        protected void onPostExecute(String result) {

//          Log.i("Response: ", result);
            showResult(result);
            TextView tvResponse = (TextView) findViewById(R.id.tvResponse);
            tvResponse.setText("Response: " + result);
            tvResponse.setFocusable(true);

        }



    }



    //Used for timer
    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }

    //User for timer
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            final String hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)));

            long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime));


            finalTime = lastsec;
            //System.out.println(lastsec + " hms " + hms);

            //Toast.makeText(getApplicationContext(),lastsec + " hms " + hms,Toast.LENGTH_LONG).show();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (recordingTimer != null)
                            recordingTimer.setText("Recording: " + hms);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            });
        }
    }


}
