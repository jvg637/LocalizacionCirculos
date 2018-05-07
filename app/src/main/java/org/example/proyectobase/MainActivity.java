package org.example.proyectobase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2,
        LoaderCallbackInterface, LocationListener, GpsStatus.Listener, SensorEventListener {

    float[] mGravity;
    float[] mGeomagnetic;
    float orientation[] = new float[3];
    float pitch;
    float azimut;
    float roll;


    //////////////////////////////////////////////////////////////////////////////
    //// ACELEROMETRO. CALCULA ÁNGULO DE ROTACION
    //////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent evento) {
        float acelerometroX = -1;
        float acelerometroY = -1;
        float acelerometroZ = -1;

        acelerometroX = evento.values[0];
        acelerometroY = evento.values[1];
        acelerometroZ = evento.values[2];

        int anguloARotar = 0;

//        if (acelerometroX > 6.5) {
//            anguloARotar = 0;
//
//            procesador.setInicioY ((int) (cam_altura * 1.0 / 6.0));
//            procesador.setFinY((int) (cam_altura / 2.1 + procesador.getInicioY()));
//
//            procesador.setInicioX((int) (cam_anchura * 2.2 / 6.0));
//            procesador.setFinX((int) (cam_anchura / 2.1 + procesador.getInicioX()));
//
//        } else if (acelerometroY > 6.5) {
//            anguloARotar = -90;
//
//            procesador.setInicioY ((int) (cam_altura * 1.0 / 6.0));
//            procesador.setFinY ( (int) (cam_altura / 2.1 + procesador.getInicioY()));
//
//            procesador.setInicioX ((int) (cam_anchura * 1.0 / 6.0));
//            procesador.setFinX((int) (cam_anchura / 2.1 + procesador.getInicioX()));
//
//        } else if (acelerometroX < -6.5) {
//            anguloARotar = -180;
//
//            procesador.setInicioY ((int) (cam_altura * 2.1 / 6.0));
//            procesador.setFinY ((int) (cam_altura / 2.1 + procesador.getInicioY()));
//
//            procesador.setInicioX  ((int) (cam_anchura * 1.0 / 6.0));
//            procesador.setFinX ((int) (cam_anchura / 2.1 + procesador.getInicioX()));
//
//        } else if (acelerometroY < -6.5) {
//            anguloARotar = 90;
//
//            procesador.setInicioY ((int) (cam_altura * 2.2 / 6.0));
//            procesador.setFinY((int) (cam_altura / 2.1 + procesador.getInicioY()));
//
//            procesador.setInicioX((int) (cam_anchura * 2.2 / 6.0));
//            procesador.setFinX((int) (cam_anchura / 2.1 + procesador.getInicioX()));
//        } else {
//            anguloARotar = 0;
//
//            procesador.setInicioY((int) (cam_altura * 1.0 / 6.0));
//            procesador.setFinY((int) (cam_altura / 2.1 + procesador.getInicioY()));
//
//            procesador.setInicioX((int) (cam_anchura * 2.2 / 6.0));
//            procesador.setFinX((int) (cam_anchura / 2.1 + procesador.getInicioX()));
//        }
//        if (procesador != null) {
//            if (tipoEntrada == 0)
//                procesador.setAnguloARotar(anguloARotar);
//            else
//                procesador.setAnguloARotar(0);
//
//        }
    }

    public interface ObserverVelocity {
        void actualizaVelocidadLeida(int velocidad);
    }

    private final long TIEMPO_MIN = 5000; // 5 segundos
    private final long DISTANCIA_MIN = 5; // 5 metros
    private LocationManager locationManager;


    public static boolean logOn = true;

    private static final String TAG = "(MainActivity)";
    private CameraBridgeViewBase cameraView;


    private int indiceCamara; // 0-> camara trasera; 1-> camara frontal
    public static int cam_anchura = 800;// resolucion deseada de la imagen
    public static int cam_altura = 600;
    private static final String STATE_CAMERA_INDEX = "cameraIndex";


    private int tipoEntrada = 0; // 0 -> cámara 1 -> fichero1 2 -> fichero2
    Mat imagenRecurso_;
    boolean recargarRecurso = false;
    private boolean dividirImagen = false;
    public static boolean aumentoLineal = false;
    private boolean imagenColor = true;
    private TextView txtCurrentSpeed;
    private ImageView txtCurrentaLastSpeedRed;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
// Save the current camera index.
        savedInstanceState.putInt(STATE_CAMERA_INDEX, indiceCamara);
        super.onSaveInstanceState(savedInstanceState);
    }

    private float speed = 0.0f;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();

//        View contenedor = (View) findViewById(R.id.contenedor);
//        registerForContextMenu(contenedor);
        txtCurrentSpeed = (TextView) this.findViewById(R.id.txtCurrentSpeed);
        txtCurrentaLastSpeedRed = (ImageView) this.findViewById(R.id.txtCurrentaLastSpeedRed);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        cameraView = (CameraBridgeViewBase) findViewById(R.id.vista_camara);
        cameraView.setCvCameraViewListener(this);

        if (savedInstanceState != null) {
            indiceCamara = savedInstanceState.getInt(STATE_CAMERA_INDEX, 0);
        } else {
            indiceCamara = CameraBridgeViewBase.CAMERA_ID_BACK;
        }
        cameraView.setCameraIndex(indiceCamara);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
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


    private static final int SOLICITUD_PERMISOS = 0;

    void solicitarPermisos() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    SOLICITUD_PERMISOS);
        } else {
            inicializaAplicacion();
        }
    }

    private void inicializaAplicacion() {
        // permission denied, boo! Disable the
        // functionality that depends on this permission.
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this,
                this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (locationManager != null) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            speed = (lastKnownLocation != null) ? lastKnownLocation.getSpeed() * 3.6f : 0.0f;
            this.updateSpeed(speed, 0);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_MIN, DISTANCIA_MIN, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SOLICITUD_PERMISOS: {
                // If request is cancelled, the result arrays are empty.

                if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        ||
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        ||
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Se deben aceptar todos los permisos para inicializar la aplicación", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    inicializaAplicacion();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void calculaCoordenadasZoom() {
        procesador.setInicioY((int) (cam_altura * 1.0 / 6.0));
        procesador.setFinY((int) (cam_altura / 2.1 + procesador.getInicioY()));

        procesador.setInicioX((int) (cam_anchura * 2.2 / 6.0));
        procesador.setFinX((int) (cam_anchura / 2.1 + procesador.getInicioX()));

    }

    //Interface CvCameraViewListener2
    //Inicio
    @Override
    public void onCameraViewStarted(int width, int height) {
        cam_altura = height; //Estas son las que se usan de verdad
        cam_anchura = width;


        procesador = new Procesador(this, new ObserverVelocity() {
            @Override
            public void actualizaVelocidadLeida(int velocidadLeida) {
                updateSpeed(speed, velocidadLeida);
            }
        });
        calculaCoordenadasZoom();


        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        ////pantallaPartida = (preferencias.getBoolean("pantalla_partida", false));

        String valor = preferencias.getString("salida", Procesador.Salida.BINARIZACION_PREPROCESO.name());
        procesador.setMostrarSalida(Procesador.Salida.valueOf(valor));
//
//        valor = preferencias.getString("intensidad", Procesador.TipoIntensidad.AUMENTO_LINEAL_CONTRASTE.name());
//        procesador.setTipoIntensidad(Procesador.TipoIntensidad.valueOf(valor));

        valor = preferencias.getString("preproceso", Procesador.TipoPreproceso.GRADIENTE_MORFOLOGICO_DILATACION.name());
        procesador.setTipoPreProceso(Procesador.TipoPreproceso.valueOf(valor));

        valor = preferencias.getString("binarizacion_preproceso", Procesador.TipoBinarizacion.ADAPTATIVA.name());
        procesador.setTipoBinarizacionPreProceso(Procesador.TipoBinarizacion.valueOf(valor));

        valor = preferencias.getString("segmentacion_disco", Procesador.TipoSegmentacionCirculo.COMPONENTE_ROJA.name());
        procesador.setTipoSegmentacionCirculo(Procesador.TipoSegmentacionCirculo.valueOf(valor));

        valor = preferencias.getString("binarizacion_segmentacion_disco", Procesador.TipoBinarizacion.OTSU_INV.name());
        procesador.setTipoBinarizacionDisco(Procesador.TipoBinarizacion.valueOf(valor));

        valor = preferencias.getString("prioridad_deteccion", Procesador.TipoPrioridadDeteccionVelocidad.VELOCIDAD.name());
        procesador.setTipoPrioridadDeteccionVelocidad(Procesador.TipoPrioridadDeteccionVelocidad.valueOf(valor));
//
        valor = preferencias.getString("estabilidad", "2");
        procesador.setEstabilizacion(Integer.parseInt(valor));
//
        boolean valorBoleean = preferencias.getBoolean("zoom", false);
        procesador.setZoom(valorBoleean);

        entrada = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        entrada.release();
        if (salida != null)
            salida.release();
    }

    private int RECURSOS_FICHEROS[];

    private Mat entrada, salida;
    private Procesador procesador;

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        if (tipoEntrada == 0) {
            if (imagenRecurso_ != null)
                imagenRecurso_.release();

            entrada = inputFrame.rgba();

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
            if (!imagenColor && imagenRecurso_.channels() > 1) {
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

        if (locationManager != null)
            locationManager.removeUpdates(this);

        initListenersOrientation();
    }

    private void initListenersOrientation() {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
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
            case R.id.preferencias:
                Intent i = new Intent(this, Preferencias.class);
                startActivity(i);
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


    ///////////////////////////////////////////////////////////////////////////////////////////////

    /***************************************** GPS ***********************************************/
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if (location != null) {
            speed = location.getSpeed() * 3.6f;
            this.updateSpeed(speed, procesador.getLastSpeedRed());
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onGpsStatusChanged(int event) {
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


    public void updateSpeed(final float speed, final int lastSpeedRed) {
        // TODO Auto-generated method stub
        txtCurrentSpeed.post(new Runnable() {
            @Override
            public void run() {
                txtCurrentSpeed.setText(String.format("Vel:% 4.1f", speed) + " " + strUnits);
            }
        });

        txtCurrentaLastSpeedRed.post(new Runnable() {
            @Override
            public void run() {
                switch (lastSpeedRed) {
                    case 10:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v10);
                        break;
                    case 20:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v20);
                        break;
                    case 30:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v30);
                        break;
                    case 40:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v40);
                        break;
                    case 50:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v50);
                        break;
                    case 60:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v60);
                        break;
                    case 70:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v70);
                        break;
                    case 80:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v80);
                        break;
                    case 90:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v90);
                        break;
                    case 100:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v100);
                        break;
                    case 110:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v110);
                        break;
                    case 120:
                        txtCurrentaLastSpeedRed.setBackgroundResource(R.drawable.v120);
                        break;
                }
//                txtCurrentaLastSpeedRed.set(String.format("% 4d", lastSpeedRed) + " " + strUnits);
            }
        });
    }

    final String strUnits = "km/h";
}
