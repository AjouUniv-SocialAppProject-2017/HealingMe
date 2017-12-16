package kr.ac.ajou.healingme;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class DrawActivity extends Fragment{

    private ImageView image;
    private  RelativeLayout relativeLayout;
    private DrawView mDrawView;
    public static float mStrokeWidth = 3;
    public static int mStrokeColor = Color.BLACK;
    public static int mBackColor = Color.WHITE;
    private Button btn_pen,btn_pencolor,btn_eraser;

    final int REQ_CODE_SELECT_IMAGE=100;
    static Bitmap image_bitmap;
    private static final int My_Permission_Storage=1111;




    public DrawActivity() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_draw, container, false);

        final RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.rect);
        relativeLayout.addView(new DrawView(getActivity()));


/*      mDrawView=new DrawView(getContext());

        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(784,900);
        mDrawView.setLayoutParams(params);
        mDrawView.setBottom(100);*/

        Log.e("Height", container.getHeight() + "");
        Log.e("Width", container.getWidth() + "");
        container.getWidth();


        if (savedInstanceState != null) {
            // 화면전환 전에 넣어주었던 pointList를 꺼내서 세팅
            relativeLayout.addView(new DrawView(getActivity()));

            mDrawView.pointList =
                    (ArrayList<DrawView.Point>) savedInstanceState.getSerializable("list");
        }

        btn_pen = (Button) rootView.findViewById(R.id.btn_pen);
        btn_pencolor = (Button) rootView.findViewById(R.id.btn_pencolor);
        btn_eraser = (Button) rootView.findViewById(R.id.btn_eraser);

        btn_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("펜");
                final String[] Pen = new String[]{
                        "얇은 펜","중간펜","굵은펜"
                };

                builder.setTitle("펜굵기를 선택하세요")        // 제목 설정
                        .setItems(Pen, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index) {
                                if (index == 0) {//검
                                    mStrokeWidth=3;
                                    relativeLayout.invalidate();
                                } else if (index == 1) {//빨
                                    mStrokeWidth=7;
                                    relativeLayout.invalidate();
                                } else{//주
                                    mStrokeWidth=11;
                                    relativeLayout.invalidate();
                                }
                            }
                        });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
            }
        });

        btn_pencolor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("펜의 색을 선택하세요");
                final String[] PenColor = new String[]{
                        "검정색", "빨간색", "주황색", "분홍색", "파란색", "보라색", "초록색", "노란색", "흰색"
                };

                builder.setTitle("펜의 색을 선택하세요")        // 제목 설정
                        .setItems(PenColor, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index) {
                                if (index == 0) {//검
                                    mStrokeColor = Color.BLACK;
                                    relativeLayout.invalidate();
                                } else if (index == 1) {//빨
                                    mStrokeColor = Color.RED;
                                    relativeLayout.invalidate();
                                } else if (index == 2) {//주
                                    mStrokeColor = Color.rgb(255, 165, 0);
                                    relativeLayout.invalidate();
                                } else if (index == 3) {//핑
                                    mStrokeColor = Color.MAGENTA;
                                    relativeLayout.invalidate();
                                } else if (index == 4) {//파
                                    mStrokeColor = Color.BLUE;
                                    relativeLayout.invalidate();
                                } else if (index == 5) {//보
                                    mStrokeColor = Color.rgb(147, 112, 219);
                                    relativeLayout.invalidate();
                                } else if (index == 6) {//초
                                    mStrokeColor = Color.GREEN;
                                    relativeLayout.invalidate();
                                } else if (index == 7) {//노
                                    mStrokeColor = Color.YELLOW;
                                    relativeLayout.invalidate();
                                } else {//흰
                                    mStrokeColor = Color.WHITE;
                                    relativeLayout.invalidate();
                                }
                            }
                        });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
            }
        });

        btn_eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("기타");
                final String[] Eraser = new String[]{
                        "전체삭제", "저장하기",
                };

                builder.setTitle("삭제 또는 저장하기를 선택하세요")        // 제목 설정
                        .setItems(Eraser, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index) {
                                if (index == 0) {
                                    relativeLayout.removeAllViews();
                                    relativeLayout.invalidate();
                                    relativeLayout.addView(new DrawView(getActivity()));

                                } else if (index == 1) {
                                    checkPermission();
                                    relativeLayout.setDrawingCacheEnabled(true);    // 캐쉬허용
                                    // 캐쉬에서 가져온 비트맵을 복사해서 새로운 비트맵(스크린샷) 생성
                                    Bitmap screenshot = Bitmap.createBitmap(relativeLayout.getDrawingCache());
                                    relativeLayout.setDrawingCacheEnabled(false);   // 캐쉬닫기

                                    //비트맵을 jpg로 저장
                                    String image_name="my";
                                    String root = Environment.getExternalStorageDirectory().toString();
                                    File myDir = new File(root);
                                    myDir.mkdirs();

                                    String fname = "Image-" + image_name+ ".jpg";
                                    File file = new File(myDir, fname);
                                    if (file.exists()) file.delete();
                                    Log.i("LOAD", root + fname);
                                    try {
                                        FileOutputStream out = new FileOutputStream(file);
                                        screenshot.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                        out.flush();
                                        out.close();
                                        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), screenshot, "title", "descripton");//갤러리로 저장
                                        Toast.makeText(getContext(),"그림이 저장되었습니다",Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private static class DrawView extends View implements View.OnTouchListener {

        float x;
        float y;
        public ArrayList<Point> pointList = new ArrayList<Point>();

        public DrawView(Context context) {
            super(context);
            setOnTouchListener(this);
            setFocusableInTouchMode(true);  // 이벤트가 계속해서 발생하기 위해
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Log.e("CanvasHeight", canvas.getHeight()+"");
            Log.e("CanvasWidth", canvas.getWidth()+"");

            canvas.drawColor(mBackColor);
            mBackColor = Color.WHITE;

            canvas.drawColor(Color.WHITE);

            Paint paint = new Paint();

            if(pointList.size() < 2) return;
            for (int i=1; i<pointList.size(); i++) {

                if (pointList.get(i).draw) {
                    paint.setColor(pointList.get(i).mStrokeColor);
                    paint.setStrokeWidth(pointList.get(i).mStrokeWidth);
                    canvas.drawLine(pointList.get(i - 1).x,
                            pointList.get(i - 1).y, pointList.get(i).x,
                            pointList.get(i).y, paint);
                }
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            x = event.getX();
            y = event.getY();

             float mX, mY;

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("phoro", "손가락으로 터치했음");
                    pointList.add(new Point(x,y,false,mStrokeWidth,mStrokeColor));
                    invalidate();         // 그림 다시 그리기
                    return true;                // 이벤트가 여기에서 끝난다.

                case MotionEvent.ACTION_MOVE:
                    Log.d("phoro", "손가락으로 움직이는 중");
                    pointList.add(new Point(x,y,true,mStrokeWidth,mStrokeColor));
                    invalidate();         // 그림 다시 그리기
                    return true;

                case MotionEvent.ACTION_UP:
                    Log.d("phoro", "손가락 땠음");

                default:

            }


            return false;
        }//end class DrawView



        static class Point implements Serializable {
            float x,y;
            boolean draw;
            float mStrokeWidth;
            int mStrokeColor;

            public Point(float x,float y,boolean draw,float mStrokeWidth,int mStrokeColor) {
                this.x = x;
                this.y = y;
                this.draw = draw;
                this.mStrokeColor = mStrokeColor;
                this.mStrokeWidth = mStrokeWidth;
            }

        }//end class Point
    }



    //권한설정
    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)){
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("알림")
                        .setMessage("권한 설정을 해주세요")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:"+getActivity().getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("거부", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }else {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},My_Permission_Storage);

            }

        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case My_Permission_Storage:
                for(int i=0;i<grantResults.length;i++){
                    if(grantResults[i]<0){
                        Toast.makeText(getContext(),"해당 권한을 설정해주세요",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                break;
        }

    }

}
