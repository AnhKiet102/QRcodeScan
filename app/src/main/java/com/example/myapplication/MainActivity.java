package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.QRGeoModel;
import com.example.myapplication.Model.QRUrlModel;
import com.example.myapplication.Model.QRVcardModel;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init
        scannerView = (ZXingScannerView)findViewById(R.id.zxcan);
        txtResult=(TextView)findViewById(R.id.txt_result);
        //Request permission
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(MainActivity.this);
                        scannerView.startCamera();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "You must acept this permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }


    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        //here wecan see Rawresult
        processRawResult(rawResult.getText());
        scannerView.startCamera();

    }

    private void processRawResult(String text) {
        if(text.startsWith("BEGIN:"))
        {
            String[] tokens= text.split("\n");
            QRVcardModel qrVcardModel= new QRVcardModel();
            for(int i =0; i < tokens.length; i++ )
            {
                if(tokens[i].startsWith("BEGIN:"))
                {
                    qrVcardModel.setType(tokens[i].substring("BEGIN:".length())); // remove begin
                }
                else if(tokens[i].startsWith("N:"))
                {
                    qrVcardModel.setName(tokens[i].substring("N:".length())); // remove begin
                }
                else if(tokens[i].startsWith("ORG:"))
                {
                    qrVcardModel.setOrg(tokens[i].substring("ORG:".length())); // remove begin
                }
                else if(tokens[i].startsWith("TEL:"))
                {
                    qrVcardModel.setTel(tokens[i].substring("TEL:".length())); // remove begin
                }
                else if(tokens[i].startsWith("URL:"))
                {
                    qrVcardModel.setUrl(tokens[i].substring("URL:".length())); // remove begin
                }
                else if(tokens[i].startsWith("EMAIL:"))
                {
                    qrVcardModel.setEmail(tokens[i].substring("EMAIL:".length())); // remove begin
                }
                else if(tokens[i].startsWith("ADR:"))
                {
                    qrVcardModel.setAddress(tokens[i].substring("ADR:".length())); // remove begin
                }
                else if(tokens[i].startsWith("NOTE:"))
                {
                    qrVcardModel.setNote(tokens[i].substring("NOTE:".length())); // remove begin
                }
                else if(tokens[i].startsWith("SUMARY:"))
                {
                    qrVcardModel.setSummary(tokens[i].substring("SUMARY:".length())); // remove begin
                }
                else if(tokens[i].startsWith("DTSTART:"))
                {
                    qrVcardModel.setDtstart(tokens[i].substring("DTSTART:".length())); // remove begin
                }
                else if(tokens[i].startsWith("DTEND:"))
                {
                    qrVcardModel.setDtend(tokens[i].substring("DTEND:".length())); // remove begin
                }
                //try to show
                txtResult.setText(qrVcardModel.getType());
            }
        }
        else if(text.startsWith("http://")||
                     text.startsWith("https://")||
                text.startsWith("www."))
        {
            QRUrlModel qrUrlModel= new QRUrlModel(text);
            txtResult.setText(qrUrlModel.getUrl());

        }
        else if(text.startsWith("geo:"))
        {
            QRGeoModel qrGeoModel=new QRGeoModel();
            String delims="[ , ?q= ]+";
            String tokens[]=text.split(delims);
            for(int i=0; i< tokens.length;i++)
            {
                if(text.startsWith("geo:"))
                {
                    qrGeoModel.setLat(tokens[i].substring("geo:".length()));
                }
            }
            qrGeoModel.setLat(tokens[0].substring("geo:".length()));
            qrGeoModel.setLng(tokens[1]);
            qrGeoModel.setGeo_place(tokens[2]);
            txtResult.setText(qrGeoModel.getLat()+"/"+qrGeoModel.getLng());
        }
        else
        {
            txtResult.setText(text);
        }

        scannerView.resumeCameraPreview(MainActivity.this);
    }
}
