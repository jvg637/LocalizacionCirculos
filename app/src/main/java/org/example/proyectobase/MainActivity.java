package org.example.proyectobase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2,
        LoaderCallbackInterface {

    public static boolean logOn = true;

    private static final String TAG = "(MainActivity)";
    private CameraBridgeViewBase cameraView;


    private int indiceCamara; // 0-> camara trasera; 1-> camara frontal
    private int cam_anchura = 320;// resolucion deseada de la imagen
    private int cam_altura = 240;
    private static final String STATE_CAMERA_INDEX = "cameraIndex";


    private int tipoEntrada = 0; // 0 -> cámara 1 -> fichero1 2 -> fichero2
    Mat imagenRecurso_;
    boolean recargarRecurso = false;
    private boolean dividirImagen = false;
    public static boolean aumentoLineal = false;
    private boolean imagenColor = true;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
// Save the current camera index.
        savedInstanceState.putInt(STATE_CAMERA_INDEX, indiceCamara);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();

//        View contenedor = (View) findViewById(R.id.contenedor);
//        registerForContextMenu(contenedor);

        cameraView = (CameraBridgeViewBase) findViewById(R.id.vista_camara);
        cameraView.setCvCameraViewListener(this);

        if (savedInstanceState != null) {
            indiceCamara = savedInstanceState.getInt(STATE_CAMERA_INDEX, 0);
        } else {
            indiceCamara = CameraBridgeViewBase.CAMERA_ID_BACK;
        }
        cameraView.setCameraIndex(indiceCamara);
    }


    private void listarFicherosRaw() {
        SubMenu subMenu = imagenes.getSubMenu();
        subMenu.clear();
        subMenu.add(1, 1, 0, "camara");
        Field[] fields = R.raw.class.getFields();

        List<Integer> vector = new ArrayList<>();
        vector.add(0);

        for (int count = 0; count < fields.length; count++) {
            Log.i("Raw Asset: ", fields[count].getName());
            subMenu.add(1, 1, count + 1, "img:" + fields[count].getName());
            try {
                vector.add(fields[count].getInt(fields[count]));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        RECURSOS_FICHEROS = new int[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            RECURSOS_FICHEROS[i] = vector.get(i);
        }
    }


    private static final int SOLICITUD_PERMISO_CAMERA = 0;

    void solicitarPermisos() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                        SOLICITUD_PERMISO_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        SOLICITUD_PERMISO_CAMERA);
            }
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this,
                    this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SOLICITUD_PERMISO_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this,
                            this);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //Interface CvCameraViewListener2
    //Inicio
    @Override
    public void onCameraViewStarted(int width, int height) {
        cam_altura = height; //Estas son las que se usan de verdad
        cam_anchura = width;

        procesador = new Procesador();
        // Lector de texto
        procesador.inicializaVoz(getBaseContext());

    }

    @Override
    public void onCameraViewStopped() {

    }

    private int RECURSOS_FICHEROS[];

    private Mat entrada, salida;
    private Procesador procesador;

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        if (tipoEntrada == 0) {
            if (imagenRecurso_ != null)
                imagenRecurso_.release();

            if (imagenColor)
                entrada = inputFrame.rgba();
            else
                entrada = inputFrame.gray();

        } else {
            if (recargarRecurso == true) {
                if (imagenRecurso_ != null)
                    imagenRecurso_.release();
                else
                    imagenRecurso_ = new Mat();
                //Poner aqui el nombre de los archivos copiados

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        RECURSOS_FICHEROS[tipoEntrada]);
                //Convierte el recurso a una Mat de OpenCV
                Utils.bitmapToMat(bitmap, imagenRecurso_);
                Imgproc.resize(imagenRecurso_, imagenRecurso_,
                        new Size(cam_anchura, cam_altura));
                recargarRecurso = false;


            }
            if (!imagenColor && imagenRecurso_.channels()>1) {
                Imgproc.cvtColor(imagenRecurso_, imagenRecurso_, Imgproc.COLOR_RGBA2GRAY);
            }
            entrada = imagenRecurso_;
        }

        if (salida != null)
            salida.release();

        salida = procesador.procesa(entrada);

        if (salida.channels() == 1)
            Imgproc.cvtColor(salida, salida, Imgproc.COLOR_GRAY2RGBA);

        if (dividirImagen)
            procesador.mitadMitad(entrada, salida);

        if (guardarSiguienteImagen) {//Para foto salida debe ser rgba
            takePhoto(entrada, salida);
            guardarSiguienteImagen = false;
        }
        if (tipoEntrada > 0) {
            //Es necesario que el tamaño de la salida coincida con el real de captura
            Imgproc.resize(salida, salida, new Size(cam_anchura, cam_altura));
        }

        if (tipoEntrada == 0) {
            if (salida.size().empty())
                return entrada;
            entrada.release();
        }


        return salida;
    }

    //Fin
    private void takePhoto(final Mat input, final Mat output) {
// Determina la ruta para crear los archivos
        final long currentTimeMillis = System.currentTimeMillis();
        final String appName = getString(R.string.app_name);
        final String galleryPath = Environment
                .getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString();
        final String albumPath = galleryPath + "/" + appName;
        final String photoPathIn = albumPath + "/In_" + currentTimeMillis + ".png";
        final String photoPathOut = albumPath + "/Out_" + currentTimeMillis
                + ".png";
// Asegurarse que el directorio existe
        File album = new File(albumPath);
        if (!album.exists() && !album.mkdirs()) {
            Log.e(TAG, "Error al crear el directorio " + albumPath);
            return;
        }
// Intenta crear los archivos
        Mat mBgr = new Mat();
        if (output.channels() == 1)
            Imgproc.cvtColor(output, mBgr, Imgproc.COLOR_GRAY2BGR, 3);
        else
            Imgproc.cvtColor(output, mBgr, Imgproc.COLOR_RGBA2BGR, 3);
        if (!Imgcodecs.imwrite(photoPathOut, mBgr)) {
            Log.e(TAG, "Fallo al guardar " + photoPathOut);
        }
        if (input.channels() == 1)
            Imgproc.cvtColor(input, mBgr, Imgproc.COLOR_GRAY2BGR, 3);
        else
            Imgproc.cvtColor(input, mBgr, Imgproc.COLOR_RGBA2BGR, 3);
        if (!Imgcodecs.imwrite(photoPathIn, mBgr))
            Log.e(TAG, "Fallo al guardar " + photoPathIn);
        mBgr.release();
        return;
    }

    private void listModes() {
        Camera mCamera = Camera.open();
        mCamera.lock();
        Camera.Parameters params = mCamera.getParameters();

// Check what resolutions are supported by your camera
        List<Camera.Size> sizes = params.getSupportedVideoSizes();

// Iterate through all available resolutions and choose one.
// The chosen resolution will be stored in mSize.
        Camera.Size mSize;

        resolucion.getSubMenu().clear();
        for (Camera.Size size : sizes) {
            Log.i(TAG, "Available resolution: " + size.width + " " + size.height);
            resolucion.getSubMenu().add(size.width + "x" + size.height);
        }
        mCamera.unlock();


    }

    //Interface LoaderCallbackInterface
    //Inicio
    @Override
    public void onManagerConnected(int status) {
        switch (status) {
            case LoaderCallbackInterface.SUCCESS:
                Log.i(TAG, "OpenCV se cargo correctamente");
//                listModes();
                cameraView.setMaxFrameSize(cam_anchura, cam_altura);
                cameraView.enableView();
                break;
            default:
                Log.e(TAG, "OpenCV no se cargo");
                Toast.makeText(MainActivity.this, "OpenCV no se cargo",
                        Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    @Override
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {

    }
    // Fin

    // Métodos del ciclo de vida de la actividad
    @Override
    public void onResume() {
        super.onResume();
        solicitarPermisos();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraView != null)
            cameraView.disableView();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        openOptionsMenu();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        resolucion = menu.findItem(R.id.cambiarResolucion);
        imagenes = menu.findItem(R.id.cambiar_entrada);

        listarFicherosRaw();
        return true;
    }

    private MenuItem resolucion;
    private MenuItem imagenes;

    private boolean guardarSiguienteImagen = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cambiarCamara:
                if (indiceCamara == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    indiceCamara = CameraBridgeViewBase.CAMERA_ID_FRONT;
                } else indiceCamara = CameraBridgeViewBase.CAMERA_ID_BACK;
                recreate();
                break;
            case R.id.resolucion_800x600:
                cam_anchura = 800;
                cam_altura = 600;
                reiniciarResolucion();
                break;
            case R.id.resolucion_640x480:
                cam_anchura = 640;
                cam_altura = 480;
                reiniciarResolucion();
                break;
            case R.id.resolucion_320x240:
                cam_anchura = 320;
                cam_altura = 240;
                reiniciarResolucion();
                break;
            case R.id.guardar_imagenes:
                guardarSiguienteImagen = true;
                break;
            case R.id.entrada_imagen_color:
                if (item.isChecked()) {
                    imagenColor = false;
                    item.setChecked(false);
                } else {
                    imagenColor = true;
                    item.setChecked(true);
                }
                recargarRecurso = true;
                break;
            case R.id.dividir_pantalla:
                if (item.isChecked()) {
                    dividirImagen = false;
                    item.setChecked(false);
                } else {
                    dividirImagen = true;
                    item.setChecked(true);
                }
                break;
            case R.id.aumento_lineal:
                if (item.isChecked()) {
                    aumentoLineal = false;
                    item.setChecked(false);
                } else {
                    aumentoLineal = true;
                    item.setChecked(true);
                }
                break;
            default:
                String titulo = item.getTitle().toString();
                if (titulo.startsWith("img:")) {
                    String imagen = item.getTitle().toString().split(":")[1];
                    tipoEntrada = item.getOrder();
                    recargarRecurso = true;
                } else {
                    if (titulo.startsWith("camara")) {
                        tipoEntrada = 0;
                    }
                }
        }
        String msg = "W=" + Integer.toString(cam_anchura) + " H= " +
                Integer.toString(cam_altura) + " Cam= " +
                Integer.toBinaryString(indiceCamara);
        Toast.makeText(MainActivity.this, msg,
                Toast.LENGTH_SHORT).show();
        return true;
    }

    public void reiniciarResolucion() {
        recargarRecurso = true;
        cameraView.disableView();
        cameraView.setMaxFrameSize(cam_anchura, cam_altura);
        cameraView.enableView();
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        switch (item.getItemId()) {
////            case R.id.edit:
////                editNote(info.id);
////                return true;
////            case R.id.delete:
////                deleteNote(info.id);
////                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }
}
