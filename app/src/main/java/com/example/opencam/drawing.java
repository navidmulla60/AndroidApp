package com.example.opencam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class drawing extends AppCompatActivity {
    //ImageView editImage;
    String imagedata;
    Uri uri;
    Button rectBtn, pointBtn;
    Button drawpoint ;
    EditText x_value,y_value;
    ImageView imageResult;
    Bitmap tempBitmap,bitmapMaster;
    Canvas canvasMaster,canvasDrawingPane,tempImage;
    TextView textSource;
    projectPt startPt;
    Point rectStartPt,endPt,point;
    List<Point> point_store = new ArrayList<>();

    int i=0;

    ImageView imageDrawingPane;
    Bitmap bitmapDrawingPane;
    final int RQS_IMAGE1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        imageResult = findViewById(R.id.result);
        textSource = (TextView) findViewById(R.id.sourceuri);
        pointBtn=(Button) findViewById(R.id.button2);
        x_value=(EditText) findViewById(R.id.editTextNumber);
        y_value=(EditText) findViewById(R.id.editTextNumber2);
        final boolean[] isRectDrawn = {false};

        //editImage=findViewById(R.id.editImage);
        imagedata = getIntent().getStringExtra("image_path");
        uri = Uri.parse(imagedata);
        /*editImage.setImageURI(uri);*/

        drawImage(uri);

        //..............................for drawing Rectangle...................
        rectBtn = findViewById(R.id.drawrect);
        rectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = point_store.size();
                //textSource.setText("point 1 is:  "+point_store.get(0));
               /* textSource.setText("point 2 is :  "+point_store.get(1));
                textSource.setText("point 3 is:  "+point_store.get(2));
                textSource.setText("point 4 is:  "+point_store.get(3));*/



                imageResult.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        int action = event.getAction();
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        if(isRectDrawn[0] ==true){
                            drawImage(uri);
                            drawPointOnImage();

                        }
                        int xx;
                        int yy;
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                xx= 0;
                                yy= 0;
                                textSource.setText("ACTION_DOWN- " + x + " : " + y);
                                startPt = (projectPt) projectXY((ImageView) view, bitmapMaster, x, y);

                                rectStartPt= new Point(startPt.x,startPt.y);
                                break;
                            case MotionEvent.ACTION_MOVE:

                                textSource.setText("ACTION_MOVE- " + x + " : " + y);
                                drawOnRectProjectedBitMap((ImageView) view, bitmapMaster, x, y);
                                drawPointOnImage();
                                break;
                            case MotionEvent.ACTION_UP:
                                textSource.setText("ACTION_UP- " + x + " : " + y);
                                drawOnRectProjectedBitMap((ImageView) view, bitmapMaster, x, y);
                                drawPointOnImage();
                                isRectDrawn[0] =true;
                                finalizeDrawing();

                                //check point inside rectangle
                                startPt = (projectPt) projectXY((ImageView) view, bitmapMaster, x, y);
                                endPt= new Point(startPt.x,startPt.y);

                                Paint paint = new Paint();
                                paint.setStyle(Paint.Style.STROKE);
                                //paint.setColor(Color.GREEN);
                                paint.setStrokeWidth(20);



                                for (int i = 0; i <point_store.size() ; i++) {
                                    point = point_store.get(i);
                                    xx=point.x;
                                    yy=point.y;
                                    //textSource.setText("xxx: "+xx+" yyy: "+yy);
                                    if (rectStartPt.x<endPt.x){

                                        if(xx> rectStartPt.x & xx<endPt.x & yy< rectStartPt.y & yy> endPt.y) {
                                            paint.setColor(Color.GREEN);
                                            //textSource.setText(" sx : "+rectStartPt.x+" sy: "+rectStartPt.y+"   ex: "+endPt.x+" ey: "+endPt.y +"px: "+xx+"py: "+ yy);
                                            canvasDrawingPane.drawPoint(xx, yy, paint);
                                            }
                                        }
                                    if (rectStartPt.x>endPt.x) {
                                        if (xx < rectStartPt.x & xx > endPt.x & yy > rectStartPt.y & yy < endPt.y) {
                                            paint.setColor(Color.GREEN);
                                            textSource.setText(" sx : " + rectStartPt.x + " sy: " + rectStartPt.y + "   ex: " + endPt.x + " ey: " + endPt.y + "px: " + xx + "py: " + yy);

                                            canvasDrawingPane.drawPoint(xx, yy, paint);
                                        }
                                    }

                                }



                                break;
                        }
                        return true;
                    }
                });
            }

        });


       //......................for drawing point..............................

        pointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    imageResult.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            int action = motionEvent.getAction();
                            int x = (int) motionEvent.getX();
                            int y = (int) motionEvent.getY();

                            switch (action) {
                                case MotionEvent.ACTION_DOWN:
                                    startPt = (projectPt) projectXY((ImageView) view, bitmapMaster, x, y);
                                    drawPoint((ImageView) view, bitmapMaster, x, y, true);
                                    //textSource.setText("x: "+x+" y: "+y);
                                    break;
                            /*case MotionEvent.ACTION_MOVE:
                                //drawOnRectProjectedBitMap((ImageView) view, bitmapMaster, x, y);
                                drawPoint((ImageView)view,bitmapMaster,startPt);
                                break;*/
                                case MotionEvent.ACTION_UP:
                                    //drawOnRectProjectedBitMap((ImageView) view, bitmapMaster, x, y);

                                    finalizeDrawing();
                                    break;
                            }
                            return true;
                        }
                    });

                }


            //}
        });
    }

    private void drawPointOnImage() {

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(20);
        for (int i = 0; i <point_store.size() ; i++) {
            point = point_store.get(i);

            int xx = point.x;
            int yy = point.y;

            canvasDrawingPane.drawPoint(xx, yy, paint);
        }
        imageDrawingPane.invalidate();
    }


    private boolean isEmpty(String X_value, String Y_value) {
        boolean tempX=false;
        boolean tempY=false;
        boolean isFieldEmpty;
        if(TextUtils.isEmpty(X_value)){
            x_value.setError("EMPTY");
            tempX=true;
        }
        if(TextUtils.isEmpty(Y_value)){
            x_value.setError("EMPTY");
            tempY=true;
        }
        if(tempX==true && tempY==true){
            isFieldEmpty=true;
            return true;
        }
        else{
            isFieldEmpty=false;
            return false;
        }

    }


    private void drawPoint(ImageView view, Bitmap bitmapMaster, int x, int y, boolean b) {
        if (b==true){
            if(x<0 ||y<0 || x > view.getWidth() || y > view.getHeight()){
                //outside ImageView
                textSource.setText("invalid");
                return;
            }else{
                textSource.setText("drwX: "+x+"drwY: "+y);
                int projectedX = (int)((double)startPt.x * ((double)bitmapMaster.getWidth()/(double)view.getWidth()));
                int projectedY = (int)((double)startPt.y * ((double)bitmapMaster.getHeight()/(double)view.getHeight()));

                //clear canvasDrawingPane
                canvasDrawingPane.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                try {
                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.YELLOW);
                    paint.setStrokeWidth(20);
                    canvasDrawingPane.drawPoint(startPt.x,startPt.y,paint);

                    point_store.add(new Point( startPt.x, startPt.y) );

                    //canvasDrawingPane.drawCircle(startPt.x,startPt.y,10,paint);
                    //canvasDrawingPane.drawRect(startPt.x, startPt.y, projectedX, projectedY, paint);
                    tempImage=canvasDrawingPane;
                    imageDrawingPane.invalidate();
                }catch (Error e){
                    e.printStackTrace();
                }



                //textSource.setText(startPt.x + ":  " + startPt.y);
            }
        }else{
            textSource.setText(x+"  ,  "+y);
            canvasDrawingPane.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            try {
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(30);
                canvasDrawingPane.drawCircle(x,y,10,paint);

                imageDrawingPane.invalidate();
            }catch (Error e){
                e.printStackTrace();
            }



            textSource.setText(startPt.x + ":  " + startPt.y

            );
        }


        //..........point by user click ...............
        drawpoint= (Button) findViewById(R.id.draw_point);
        drawpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String X_value= x_value.getText().toString();
                String Y_value=y_value.getText().toString();
                textSource.setText(X_value+" , " +Y_value);
                //boolean status = isEmpty(X_value, Y_value);

               // textSource.setText(String.valueOf(status));
               // if (status == false) {

                //    textSource.setText("iNSIDE");

                    //custom points
                    /*int x= Integer.parseInt(X_value);
                    int y= Integer.parseInt(Y_value);*/
                    //textSource.setText((x+ "  ,  " +y));
                    int x=500;
                    int y=500;
                    startPt = (projectPt) projectXY((ImageView)view, bitmapMaster, x, y);
                    drawPoint((ImageView) view, bitmapMaster, x, y,true);
                    finalizeDrawing();

                }
                /*else{
                    textSource.setText("in Else");
                }*/


            //}
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
                paint.setStrokeWidth(10);
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

                imageResult.setRotation(90);
                imageResult.setImageBitmap(bitmapMaster);

                //Create bitmap of same size for drawing
                bitmapDrawingPane = Bitmap.createBitmap(
                        tempBitmap.getWidth(),
                        tempBitmap.getHeight(),
                        config);

                canvasDrawingPane = new Canvas(bitmapDrawingPane);

                imageDrawingPane=findViewById(R.id.drawingpane);
                imageDrawingPane.setRotation(90);
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
            x=view.getWidth();
            y=view.getHeight();
            int projectedX = (int)((double)x * ((double)bitmapMaster.getWidth()/(double)view.getWidth()));
            int projectedY = (int)((double)y * ((double)bitmapMaster.getHeight()/(double)view.getHeight()));
            return new projectPt(projectedX,projectedY);
            //return null;
        }else{
            int projectedX = (int)((double)x * ((double)bitmapMaster.getWidth()/(double)view.getWidth()));
            int projectedY = (int)((double)y * ((double)bitmapMaster.getHeight()/(double)view.getHeight()));
            textSource.setText("ProjectXYZ-New Point Generated");
            return new projectPt(projectedX, projectedY);
        }
    }


}