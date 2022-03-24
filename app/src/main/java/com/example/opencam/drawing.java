package com.example.opencam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.util.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

public class drawing extends AppCompatActivity {
    //ImageView editImage;
    String imagedata;
    Uri uri;
    Button rectBtn;
    ImageView imageResult;
    Bitmap tempBitmap,bitmapMaster;
    Canvas canvasMaster,canvasDrawingPane;
    TextView textSource;
    projectPt startPt,endPt;



    ImageView imageDrawingPane;
    Bitmap bitmapDrawingPane;
    final int RQS_IMAGE1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        imageResult=findViewById(R.id.result);
        textSource = (TextView)findViewById(R.id.sourceuri);
        //editImage=findViewById(R.id.editImage);
        imagedata=getIntent().getStringExtra("image_path");
        uri=Uri.parse(imagedata);
        /*editImage.setImageURI(uri);*/

        drawImage(uri);

        rectBtn=findViewById(R.id.drawrect);

        rectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageResult.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        int action = event.getAction();
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        switch(action){
                            case MotionEvent.ACTION_DOWN:
                                textSource.setText("ACTION_DOWN- " + x + " : " + y);
                                startPt = (projectPt) projectXY((ImageView)view, bitmapMaster, x, y);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                textSource.setText("ACTION_MOVE- " + x + " : " + y);
                                drawOnRectProjectedBitMap((ImageView)view, bitmapMaster, x, y);
                                break;
                            case MotionEvent.ACTION_UP:
                                textSource.setText("ACTION_UP- " + x + " : " + y);
                                drawOnRectProjectedBitMap((ImageView)view, bitmapMaster, x, y);
                                endPt = (projectPt) projectXY((ImageView)view, bitmapMaster, x, y);
                                finalizeDrawing();
                                break;
                        }
                        return true;
                    }
                });

            }
        });
    }

    private void drawOnRectProjectedBitMap(ImageView view, Bitmap bitmapMaster, int x, int y) {
        if(x<0 || y<0 || x > view.getWidth() || y > view.getHeight()){
            //outside ImageView
            return;
        }else{
            int projectedX = (int)((double)x * ((double)bitmapMaster.getWidth()/(double)view.getWidth()));
            int projectedY = (int)((double)y * ((double)bitmapMaster.getHeight()/(double)view.getHeight()));

            //clear canvasDrawingPane
            canvasDrawingPane.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            try {
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.YELLOW);
                paint.setStrokeWidth(30);
                canvasDrawingPane.drawRect(startPt.x, startPt.y, projectedX, projectedY, paint);
                imageDrawingPane.invalidate();
            }catch (Error e){
                e.printStackTrace();
            }

            /*textSource.setText(x + ":" + y + "/" + view.getWidth() + " : " + view.getHeight() + "\n" +
                    projectedX + " : " + projectedY + "/" + bitmapMaster.getWidth() + " : " + bitmapMaster.getHeight()
            */
            textSource.setText(x + ":" + y

            );
        }
    }


    private void drawImage(Uri uri)  {
            try {
                //tempBitmap is Immutable bitmap,
                //cannot be passed to Canvas constructor
                tempBitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(uri));

                Bitmap.Config config;
                if(tempBitmap.getConfig() != null){
                    config = tempBitmap.getConfig();
                }else{
                    config = Bitmap.Config.ARGB_8888;
                }

                //bitmapMaster is Mutable bitmap
                bitmapMaster = Bitmap.createBitmap(
                        tempBitmap.getWidth(),
                        tempBitmap.getHeight(),
                        config);

                canvasMaster = new Canvas(bitmapMaster);

                canvasMaster.drawBitmap(tempBitmap, 0, 0, null);


                imageResult.setImageBitmap(bitmapMaster);

                //Create bitmap of same size for drawing
                bitmapDrawingPane = Bitmap.createBitmap(
                        tempBitmap.getWidth(),
                        tempBitmap.getHeight(),
                        config);

                canvasDrawingPane = new Canvas(bitmapDrawingPane);

                imageDrawingPane=findViewById(R.id.drawingpane);
                imageDrawingPane.setImageBitmap(bitmapDrawingPane);


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //tempBitmap is Immutable bitmap,
        //cannot be passed to Canvas constructor

        Bitmap.Config config;
        if(tempBitmap.getConfig() != null){
            config = tempBitmap.getConfig();
        }else{
            config = Bitmap.Config.ARGB_8888;
        }

        //bitmapMaster is Mutable bitmap
        bitmapMaster = Bitmap.createBitmap(
                tempBitmap.getWidth(),
                tempBitmap.getHeight(),
                config);

        canvasMaster = new Canvas(bitmapMaster);
        canvasMaster.drawBitmap(tempBitmap, 0, 0, null);
        canvasMaster.rotate(90);
        imageResult.setImageBitmap(bitmapMaster);

        //Create bitmap of same size for drawing
        bitmapDrawingPane = Bitmap.createBitmap(
                tempBitmap.getWidth(),
                tempBitmap.getHeight(),
                config);
        canvasDrawingPane = new Canvas(bitmapDrawingPane);
        imageDrawingPane.setImageBitmap(bitmapDrawingPane);


    }


    private void finalizeDrawing() {
        canvasMaster.drawBitmap(bitmapDrawingPane, 0, 0, null);
    }



    class projectPt{
        int x;
        int y;

        projectPt(int tx, int ty){
            x = tx;
            y = ty;
        }
    }
    @Nullable
    private Object projectXY(ImageView view, Bitmap bitmapMaster, int x, int y) {
        if(x<0 || y<0 || x > view.getWidth() || y > view.getHeight()){
            //outside ImageView
            return null;
        }else{
            int projectedX = (int)((double)x * ((double)bitmapMaster.getWidth()/(double)view.getWidth()));
            int projectedY = (int)((double)y * ((double)bitmapMaster.getHeight()/(double)view.getHeight()));

            return new projectPt(projectedX, projectedY);
        }
    }
}