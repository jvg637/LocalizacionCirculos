package org.example.proyectobase;

import android.content.Context;
import android.util.Log;

import org.example.proyectobase.utils.OCRDigito;
import org.example.proyectobase.utils.TextSpeechVelocity;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.example.proyectobase.MainActivity.cam_altura;
import static org.example.proyectobase.MainActivity.cam_anchura;


public class Procesador {
    // Tipo de Preproceso: Sobel, Gradiente Morfológico, etc
    private TipoPreproceso tipoPreProceso;
    private TipoBinarizacion tipoBinarizacionPreProceso;
    private TipoSegmentacionCirculo tipoSegmentacionCirculo;
    private OCRDigito ocrDigito;
    private TextSpeechVelocity textSpeechVelocity;
    private TipoBinarizacion tipoBinarizacionDisco;
    private boolean zoom;
    private int estabilizacion;
    private int lastSpeedRed = 0;
    private MainActivity.ObserverVelocity observerVelocity;
    private TipoPrioridadDeteccionVelocidad tipoPrioridadDeteccionVelocidad;
    private int anguloARotar;
    private int inicioX;
    private Salida mostrarSalida;

    public void setTipoPreProceso(TipoPreproceso tipoPreProceso) {
        this.tipoPreProceso = tipoPreProceso;
    }

    public TipoPreproceso getTipoPreProceso() {
        return tipoPreProceso;
    }

    public void setTipoBinarizacionPreProceso(TipoBinarizacion tipoBinarizacionPreProceso) {
        this.tipoBinarizacionPreProceso = tipoBinarizacionPreProceso;
    }

    public TipoBinarizacion getTipoBinarizacionPreProceso() {
        return tipoBinarizacionPreProceso;
    }

    public void setTipoSegmentacionCirculo(TipoSegmentacionCirculo tipoSegmentacionCirculo) {
        this.tipoSegmentacionCirculo = tipoSegmentacionCirculo;
    }

    public TipoSegmentacionCirculo getTipoSegmentacionCirculo() {
        return tipoSegmentacionCirculo;
    }

    public void setTipoBinarizacionDisco(TipoBinarizacion tipoBinarizacionDisco) {
        this.tipoBinarizacionDisco = tipoBinarizacionDisco;
    }

    public TipoBinarizacion getTipoBinarizacionDisco() {
        return tipoBinarizacionDisco;
    }

    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }

    public boolean isZoom() {
        return zoom;
    }

    public void setEstabilizacion(int estabilizacion) {
        this.estabilizacion = estabilizacion;
    }

    public int getLastSpeedRed() {
        return lastSpeedRed;
    }

    public void setLastSpeedRed(int lastSpeedRed) {
        this.lastSpeedRed = lastSpeedRed;
    }

    public void setTipoPrioridadDeteccionVelocidad(TipoPrioridadDeteccionVelocidad tipoPrioridadDeteccionVelocidad) {
        this.tipoPrioridadDeteccionVelocidad = tipoPrioridadDeteccionVelocidad;
    }

    public TipoPrioridadDeteccionVelocidad getTipoPrioridadDeteccionVelocidad() {
        return tipoPrioridadDeteccionVelocidad;
    }

    public void setAnguloARotar(int anguloARotar) {
        this.anguloARotar = anguloARotar;
    }

    public int getAnguloARotar() {
        return anguloARotar;
    }


    private int inicioY;
    private int finY;

    private int finX;

    public void setInicioY(int inicioY) {
        this.inicioY = inicioY;
    }

    public int getInicioY() {
        return inicioY;
    }

    public void setFinY(int finY) {
        this.finY = finY;
    }

    public int getFinY() {
        return finY;
    }

    public void setInicioX(int inicioX) {
        this.inicioX = inicioX;
    }

    public int getInicioX() {
        return inicioX;
    }

    public void setFinX(int finX) {
        this.finX = finX;
    }

    public int getFinX() {
        return finX;
    }

    public void setMostrarSalida(Salida mostrarSalida) {
        this.mostrarSalida = mostrarSalida;
    }

    public Salida getMostrarSalida() {
        return mostrarSalida;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    // Inicio. Confguracón opciones de procesamiento
    ///////////////////////////////////////////////////////////////////////////////////
    public enum Salida {
        ENTRADA, PREPROCESO, BINARIZACION_PREPROCESO, LOCALIZACION_CIRCULOS, SEGMENTACION_CIRCULO, RECONOCIMIENTO
    }

    public enum TipoEstrategiaPreproceso {COLOR, BLANCO_Y_NEGRO, MIXTO}

    public enum TipoIntensidadPreproceso {SIN_PROCESO, LUMINANCIA, AUMENTO_LINEAL_CONTRASTE, EQUALIZ_HISTOGRAMA}

    public enum TipoBinarizacion {SIN_PROCESO, ADAPTATIVA, MEDIA, OTSU_INV, MAXIMO}

    public enum TipoPreproceso {SIN_PROCESO, ZONAS_ROJAS, FILTRO_PASO_ALTO_NEG, GRADIENTE_MORFOLOGICO_DILATACION, SOBEL_LUMINANCIA, SOBEL_RED, FILTRO_PASO_ALTO_NEG_GAUSSIANO, SOBEL_GREEN}

    public enum TipoSegmentacionCirculo {SIN_PROCESO, COMPONENTE_ROJA, FILTRO_PASO_ALTO_NEG}

    public enum TipoReconocimientoOcr {OTSU, SIN_PROCESO}

    public enum TipoPrioridadDeteccionVelocidad {TAMANYO, VELOCIDAD}


    private ProcesadorBinarizacion procesarBinarizacion;
    private ProcesadorPreproceso procesarLocal;
    private ProcesadorIntensidad procesadorIntensidad;


    Mat salida_mitad_izquierda = null;
    Mat entrada_mitad_izquierda = null;

    Mat binaria1;
    Mat entrada_gris;


    /**
     * Le un digito a partir de un recorte de entrada en Binario
     *
     * @param entrada
     * @return
     */
    public int leerDigitoOcr(Mat entrada) {
        entrada_gris = procesadorIntensidad.toGray(entrada);
//Binarizacion Otsu
        binaria1 = procesarBinarizacion.otsuInversa(entrada_gris);
//Leer numero
        int digito = ocrDigito.leerRectangulo(binaria1);

        entrada_gris.release();
        binaria1.release();
        return digito;
    }

    private Comparator<String> comparatorStr = new Comparator<String>() {
        @Override
        public int compare(String strA, String strB) {
            return strA.compareTo(strB);
        }
    };

    public Procesador(Context context, MainActivity.ObserverVelocity observerVelocity) {
        this.observerVelocity = observerVelocity;
        procesarLocal = new ProcesadorPreproceso(TipoEstrategiaPreproceso.MIXTO);
        procesadorIntensidad = new ProcesadorIntensidad();
        procesarBinarizacion = new ProcesadorBinarizacion();
        ocrDigito = new OCRDigito();
        textSpeechVelocity = new TextSpeechVelocity();
        textSpeechVelocity.inicializaVoz(context);
        // Ordena el vector de velocidades permitidas en España
        Arrays.sort(velocidadesEspanya, comparatorStr);

        salida_mitad_izquierda = new Mat();
        entrada_mitad_izquierda = new Mat();

        binaria1 = new Mat();
        entrada_gris = new Mat();

    } //Constructor


    void dibujarResultado(Mat salida, Rect rectCirculo, int digit) {
        Point P1 = rectCirculo.tl();
        Point P2 = rectCirculo.br();

        if (zoom) {
            P1.x += inicioX;
            P1.y += inicioY;
            P2.x += inicioX;
            P2.y += inicioY;
        }
        Imgproc.rectangle(salida, P1, P2, new Scalar(255, 0, 0));
// Escribir numero
        int fontFace = 6;//FONT_HERSHEY_SCRIPT_SIMPLEX;
        double fontScale = 1;
        int thickness = 5;
        Imgproc.putText(salida, String.valueOf(digit),
                P1, fontFace, fontScale,
                new Scalar(0, 0, 0), thickness, 8, false);
        Imgproc.putText(salida, String.valueOf(digit),
                P1, fontFace, fontScale,
                new Scalar(255, 255, 255), thickness / 2, 8, false);
    }

    // Variables para la estabilidad de las tomas
    private int lecturaVelocidadAnterior = -1;
    private int numeroVecesLeida = 0;


    // Control de circulo MAX
    int tamanyoMax = -1;
    int posTamanyoMax = -1;
    int velocidadTamanyoMax = -1;
    // Control de velocidad MIN
    int posVelocidadMin = -1;
    int velocidadMin = Integer.MAX_VALUE;
    // Posicion actual del Círculo que se segmenta
    int posActual = 0;

    /*
    Inicialización de variables de detección de círculos de máximo ancho/mínima velocidad
     */
    public void inicializaVariablesControlVelocidadMinimaTamanyoMaximo() {
        tamanyoMax = -1;
        posTamanyoMax = -1;
        velocidadTamanyoMax = -1;
        // Control de velocidad MIN
        posVelocidadMin = -1;
        velocidadMin = Integer.MAX_VALUE;
        // Posicion actual del Círculo que se segmenta
        posActual = 0;
    }

    public Mat procesa(Mat entradaIn) {

        Mat entrada;
        List<Rect> rectCirculos = new ArrayList<>();
        inicializaVariablesControlVelocidadMinimaTamanyoMaximo();

        /////////////////////////////////////////////////////////////
        // Salida = entrada
        escribeLog("Salida=" + mostrarSalida.name());
        if (mostrarSalida == Salida.ENTRADA) {
            Mat salida = entradaIn.clone();
            if (zoom) {
                Imgproc.rectangle(salida, new Point(inicioX, inicioY), new Point(finX - 1, finY - 1), new Scalar(0, 0, 255), 3);
            }
            return salida;
        }
        // Comprueba si está activazo el ZOOM
        if (!zoom) {
            entrada = entradaIn;
        } else {
            entrada = entradaIn.submat(inicioY, finY, inicioX, finX);
        }

        escribeLog("Preproceso=" + tipoPreProceso.name());
        Mat salidaPreproceso = procesarLocal.preproceso(tipoPreProceso, entrada);
        if (mostrarSalida == Salida.PREPROCESO) {
            Imgproc.cvtColor(salidaPreproceso, salidaPreproceso, Imgproc.COLOR_GRAY2RGBA);
            if (zoom) {
                Imgproc.rectangle(salidaPreproceso, new Point(0, 0), new Point(salidaPreproceso.width() - 1, salidaPreproceso.height() - 1), new Scalar(0, 0, 255), 3);
                entrada.release();
            }
            Imgproc.resize(salidaPreproceso, salidaPreproceso, new Size(cam_anchura, cam_altura));
            return salidaPreproceso;
        }
        escribeLog("Binarizacion Preproceso=" + tipoBinarizacionPreProceso.name());
        Mat salidaBinarizacionPreproceso = procesarBinarizacion.binarizacionPreproceso(tipoBinarizacionPreProceso, salidaPreproceso);
        if (mostrarSalida == Salida.BINARIZACION_PREPROCESO) {
            Imgproc.cvtColor(salidaBinarizacionPreproceso, salidaBinarizacionPreproceso, Imgproc.COLOR_GRAY2RGBA);
            if (zoom) {
                Imgproc.rectangle(salidaBinarizacionPreproceso, new Point(0, 0), new Point(salidaBinarizacionPreproceso.width() - 1, salidaPreproceso.height() - 1), new Scalar(0, 0, 255), 3);
                entrada.release();
            }
            Imgproc.resize(salidaBinarizacionPreproceso, salidaBinarizacionPreproceso, new Size(cam_anchura, cam_altura));

            salidaPreproceso.release();
            return salidaBinarizacionPreproceso;
        }

        localizarCirculos(salidaBinarizacionPreproceso, rectCirculos);
        if (mostrarSalida == Salida.LOCALIZACION_CIRCULOS) {
            Imgproc.cvtColor(salidaBinarizacionPreproceso, salidaBinarizacionPreproceso, Imgproc.COLOR_GRAY2RGBA);
            for (Rect rectCirculo : rectCirculos) {
                dibujaCirculosEncontratos(salidaBinarizacionPreproceso, rectCirculo);
            }
            if (zoom) {
                Imgproc.rectangle(salidaBinarizacionPreproceso, new Point(0, 0), new Point(salidaBinarizacionPreproceso.width() - 1, salidaPreproceso.height() - 1), new Scalar(0, 0, 255), 3);
                entrada.release();
            }
            salidaPreproceso.release();
            Imgproc.resize(salidaBinarizacionPreproceso, salidaBinarizacionPreproceso, new Size(cam_anchura, cam_altura));
            return salidaBinarizacionPreproceso;
        }


        Mat salida;

        if (mostrarSalida == Salida.SEGMENTACION_CIRCULO) {
            Imgproc.cvtColor(salidaBinarizacionPreproceso, salidaBinarizacionPreproceso, Imgproc.COLOR_GRAY2RGBA);
        }

        for (Rect rectCirculo : rectCirculos) {
//            dibujaCirculosEncontratos(salida, rectCirculo);
            Mat circulo = entrada.submat(rectCirculo);
            List<Rect> rectDigits = new ArrayList<>();
            segmentarInteriorDisco(circulo, rectCirculo, rectDigits, tipoSegmentacionCirculo);

//            dibujaDigitosEncontrados(salida, rectDigits);
            // Si tengo 2 o 3 digitos, les aplico OCR
            String velocidadStr = "";
            int velocidadFinal = -1;
            if (rectDigits.size() >= 2 && rectDigits.size() <= 3) {
                for (Rect rectDigito : rectDigits) {
                    Mat digito = entrada.submat(rectDigito);

                    int velocidad = leerDigitoOcr(digito);
                    if (velocidad != -1) {
                        velocidadStr += velocidad;
                    } else {
                        // Velocidad incorrecta
                        velocidadStr = "";
                        break;
                    }
                    digito.release();
                }

                if (mostrarSalida == Salida.SEGMENTACION_CIRCULO) {
                    dibujaCirculosEncontratos(salidaBinarizacionPreproceso, rectCirculo);
                    dibujaDigitosEncontrados(salidaBinarizacionPreproceso, rectDigits);
                }
                // Dibuja los circulos encontrados en rojo
//                dibujaCirculosEncontratos(salida, rectCirculo);
                // Dibuja Digitos candidatos
//                dibujaDigitosEncontrados(salida, rectDigits);
                // Si tengo 2 o 3 digitos, les aplico OCR

                velocidadFinal = Integer.parseInt(velocidadStr);

                if (!velocidadStr.isEmpty()) {
                    if (Arrays.binarySearch(velocidadesEspanya, velocidadStr, comparatorStr) >= 0) {
                        /////////////////////////////////////////////////////////////////////////////////////////////////////
                        //// ACTUALIZA LA VELOCIDAD MINIMA Y EL TAMAÑO MÁXIMO HASTA EL MOMENTO
                        /////////////////////////////////////////////////////////////////////////////////////////////////////
                        if (rectCirculo.width > tamanyoMax) {
                            tamanyoMax = rectCirculo.width;
                            posTamanyoMax = posActual;
                            velocidadTamanyoMax = velocidadFinal;
                        }
                        if (velocidadFinal < velocidadMin) {
                            velocidadMin = velocidadFinal;
                            posVelocidadMin = posActual;
                        }
                    }
                }
            }
            circulo.release();
            posActual++;
        }

        if (mostrarSalida == Salida.SEGMENTACION_CIRCULO) {
            if (zoom) {
                Imgproc.rectangle(salidaBinarizacionPreproceso, new Point(0, 0), new Point(salidaBinarizacionPreproceso.width() - 1, salidaPreproceso.height() - 1), new Scalar(0, 0, 255), 3);
                entrada.release();
            }
            salidaPreproceso.release();
            Imgproc.resize(salidaBinarizacionPreproceso, salidaBinarizacionPreproceso, new Size(cam_anchura, cam_altura));
            return salidaBinarizacionPreproceso;
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// 7. MODULO DE VISUALIZACION DE LA SEÑAL SEGÚN PRIORIDAD SELECCIONADA EN PREFERENCIAS.
        ///    TAMBIÉN INCLUYE ESTABILIDAD
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        salida = entradaIn.clone();
        moduloVisualizacionVelocidad(salida, rectCirculos);
        if (zoom) {
            Imgproc.rectangle(salida, new Point(inicioX, inicioY), new Point(finX - 1, finY - 1), new Scalar(0, 0, 255), 3);
        }
        // Libera Memoria
        salidaPreproceso.release();
        salidaBinarizacionPreproceso.release();
        rectCirculos.clear();
        if (entrada != entradaIn) {
            entrada.release();
        }

        return salida;
    }


    private void moduloVisualizacionVelocidad(Mat salida, List<Rect> rectCirculos) {
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // MODULO DE ESTABLIDAD
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        int velocidad = (tipoPrioridadDeteccionVelocidad == TipoPrioridadDeteccionVelocidad.VELOCIDAD) ? velocidadMin : velocidadTamanyoMax;
        if (posTamanyoMax != -1) {
            if (lecturaVelocidadAnterior != velocidad) {
                lecturaVelocidadAnterior = velocidad;
                numeroVecesLeida = 1;
            } else {
                numeroVecesLeida++;
            }
        } else {
            reiniciaContadorLecturasEstabilizacion();
        }
        // Estabiliza la lectura
        if (numeroVecesLeida >= estabilizacion && posTamanyoMax >= 0) {
            //////////Log.d("LECTURAS_OK", "VELOCIDAD MAX: " + numeroVecesLeida + " " + velocidad + " TAMAÑO MAX: " + tamanyoMax);
            if (tipoPrioridadDeteccionVelocidad == TipoPrioridadDeteccionVelocidad.VELOCIDAD) {
                dibujarResultado(salida, rectCirculos.get(posVelocidadMin), velocidad);
            } else {
                dibujarResultado(salida, rectCirculos.get(posTamanyoMax), velocidad);
            }

            if (numeroVecesLeida == estabilizacion) {
                lastSpeedRed = velocidad;
                observerVelocity.actualizaVelocidadLeida(lastSpeedRed);
                textSpeechVelocity.salidaNumeroAltavoz(lastSpeedRed);
            }
        }


    }

    private void reiniciaContadorLecturasEstabilizacion() {
        lecturaVelocidadAnterior = -1;
        numeroVecesLeida = 0;
    }

    private void escribeLog(String txt) {
        Log.d("DEBUG", txt);
    }

    private void dibujaDigitosEncontrados(Mat salida, List<Rect> rectDigits) {
        for (Rect rectCirculoAux : rectDigits) {
            Imgproc.rectangle(salida, rectCirculoAux.tl(), rectCirculoAux.br(), new Scalar(0, 255, 0));
        }
    }

    private void dibujaCirculosEncontratos(Mat salida, Rect rectCirculo) {
        Imgproc.rectangle(salida, rectCirculo.tl(), rectCirculo.br(), new Scalar(0, 0, 255));
    }

    private void localizarCirculos(Mat binaria, List<Rect> rectCirculos) {
        if (binaria.channels() > 1)
            return;

        List<MatOfPoint> blobs = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(binaria, blobs, hierarchy, Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_NONE);
//        int minimumHeight = 30;
        int minimumHeight = 25;
        float maxratio = (float) 0.75;
        // Relacion circurferencia
        double maxRatioCircurferencia = 0.75;


        // Seleccionar candidatos a circulos
        for (int c = 0; c < blobs.size(); c++) {
            double[] data = hierarchy.get(0, c);
            // [next, previus, child, parent]
            int parent = (int) data[3];
            if (parent < 0) //Contorno exterior: rechazar
                continue;
            Rect BB = Imgproc.boundingRect(blobs.get(c));
// Comprobar tamaño
            if (BB.width < minimumHeight || BB.height < minimumHeight)
                continue;
// Comprobar anchura similar a altura
            float wf = BB.width;
            float hf = BB.height;
            float ratio = wf / hf;
            if (ratio < maxratio || ratio > 1.0 / maxratio)
                continue;
// Comprobar no está cerca del borde
            if (BB.x < 2 || BB.y < 2)
                continue;
            if (binaria.width() - (BB.x + BB.width) < 3 || binaria.height() -
                    (BB.y + BB.height) < 3)
                continue;

            // Comprueba que es un círculo
            double ratioCircurferencia = Math.abs(minMaxDistanceRatio(blobs.get(c)));
            if (ratioCircurferencia < maxRatioCircurferencia || ratioCircurferencia > 1.0 / maxRatioCircurferencia)
                continue;

            // Aqui cumple todos los criterios. Comprobamos circulos concéntricos
            insertarEliminandoCirculosConcentricos(rectCirculos, BB);

        } // for

        blobs.clear();
        hierarchy.release();

        return;
    }


    private void segmentarInteriorDisco(Mat color, Rect rectCirculo, List<Rect> rectDigits, TipoSegmentacionCirculo tipoSegmentacionCirculo) {

        Mat red = new Mat();
        Mat binaria;
        Mat gray = new Mat();
        Mat paNeg = new Mat();


        switch (tipoSegmentacionCirculo) {
            case SIN_PROCESO:
                return;
            case COMPONENTE_ROJA:
                Core.extractChannel(color, red, 0);
                binaria = procesarBinarizacion.otsuInversa(red);
                break;
            case FILTRO_PASO_ALTO_NEG:
                gray = procesadorIntensidad.toGray(color);
                paNeg = procesarLocal.preproceso(TipoPreproceso.FILTRO_PASO_ALTO_NEG, gray);
                binaria = procesarBinarizacion.otsu(paNeg);
                break;
            default:
                escribeLog("OPCION SEGMENTACION NO IMPLEMENTADA:" + tipoSegmentacionCirculo.name());
                return;
        }

        List<MatOfPoint> blobs = new ArrayList<>();
        Mat hierarchy = new Mat();


        Imgproc.findContours(binaria, blobs, hierarchy, Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_NONE);
//        int minimumHeight = 12;
        int minimumHeight = 8;

        // Seleccionar candidatos a circulos
        for (int c = 0; c < blobs.size(); c++) {
            Rect BB = Imgproc.boundingRect(blobs.get(c));
// Comprobar tamaño
//            if (BB.width < minimumHeight || BB.height < minimumHeight)
            if (BB.height < minimumHeight)
                continue;
// Comprobar altura mayor que anchura
            float wf = BB.width;
            float hf = BB.height;
            float ratio = wf / hf;
            if (ratio > 1)
                continue;
// Comprobar altura mayor que la tercera parte del círculo
            float hfC = rectCirculo.height;
            float hfD = BB.height;
            float ratio2 = hfC / hfD;
            if (ratio2 > 3)
                continue;
// Comprobar no está cerca del borde
            if (BB.x < 2 || BB.y < 2)
                continue;
            if (binaria.width() - (BB.x + BB.width) < 3 || binaria.height() -
                    (BB.y + BB.height) < 3)
                continue;

            insertarEliminandoRectángulosConcentricos(rectDigits, BB);
        } // for

        blobs.clear();
        hierarchy.release();
        red.release();
        gray.release();
        binaria.release();
        paNeg.release();

        if (rectDigits.size() >= 2 && rectDigits.size() <= 3) {
            // Ordena las colecciones por posición
            Collections.sort(rectDigits, new Comparator<Rect>() {
                @Override
                public int compare(Rect rect1, Rect rect2) {
                    //ascending order
                    return rect1.x - rect2.x;
                    //descending order
                    //return rect2.x - rect1.x;
                }
            });
            // El último  y el primer digito tiene que estar pegado al  borde derecho
            Rect rectP = rectDigits.get(rectDigits.size() - 1);
            float wC = rectCirculo.width;
            float wD = rectCirculo.width - (rectP.width + rectP.x);
            float maxRatioU = wC / wD;
            // El último  y el primer digito tiene que estar pegado al  borde derecho
            Rect rectU = rectDigits.get(0);
            wD = rectU.x;
            float maxRatioP = wC / wD;
            if (maxRatioP < 3.5f || maxRatioU < 3.5f) {
                rectDigits.clear();
                return;
            }
            // Añade a la coordenada X la posición del Círculo para que sean coordenadas absolutas
            for (Rect rectDigit : rectDigits) {
                rectDigit.x += rectCirculo.x;
                rectDigit.y += rectCirculo.y;
            }
        } else {
            rectDigits.clear();
        }
    }

    /**
     * Elimina los circulos concentricos
     *
     * @param circulosSalida
     * @param circuloA
     */
    private void insertarEliminandoCirculosConcentricos(List<Rect> circulosSalida, Rect circuloA) {
        final Point P1A = new Point(circuloA.x, circuloA.y);
        final Point P2A = new Point(circuloA.x + circuloA.width, circuloA.y + circuloA.height);

        for (int j = 0; j < circulosSalida.size(); j++) {
            Rect circuloB = circulosSalida.get(j);
            Point P1B = new Point(circuloB.x, circuloB.y);
            Point P2B = new Point(circuloB.x + circuloB.width, circuloB.y + circuloB.height);

            float relacionA = (float) circuloA.width / (float) circuloB.width;
            float relacionB = 1 / relacionA;
            // circuloB candidato
            boolean insertar = true;
            if (circuloA.contains(P1B) && circuloA.contains(P2B)) {
                if (relacionB > 0.5) {
                    // A es mucho más grande que B. Se queda B
                    return;
                } else {
                    // B es mucho más pequeño que A. Se borra B y se inserta A
                    circulosSalida.remove(circuloB);
                    j--;
                }
            } else {
                // circuloA candidato
                if (circuloB.contains(P1A) && circuloB.contains(P2A)) {
                    // B es mas grande que A. Se queda A
                    if (relacionB < 2) {
                        circulosSalida.remove(circuloB);
                        j--;
                    } else {
                        // A es mucho más pequeño que B. No se inserta A
                        return;
                    }
                }
            }
        }
        circulosSalida.add(new Rect(P1A, P2A));
    }


    /**
     * Elimina los rectángulos concentricos
     *
     * @param circulosSalida
     * @param circuloA
     */
    private void insertarEliminandoRectángulosConcentricos(List<Rect> circulosSalida, Rect circuloA) {
        final Point P1A = new Point(circuloA.x, circuloA.y);
        final Point P2A = new Point(circuloA.x + circuloA.width, circuloA.y + circuloA.height);

        for (int j = 0; j < circulosSalida.size(); j++) {
            Rect circuloB = circulosSalida.get(j);
            Point P1B = new Point(circuloB.x, circuloB.y);
            Point P2B = new Point(circuloB.x + circuloB.width, circuloB.y + circuloB.height);

            // circuloB candidato
            if (circuloA.contains(P1B) && circuloA.contains(P2B)) {
                // B es mayor que ha. Se elimina B
                circulosSalida.remove(circuloB);
            } else {
                // circuloA candidato
                if (circuloB.contains(P1A) && circuloB.contains(P2A)) {
                    // No se inserta A
                    return;
                }

            }
        }
        circulosSalida.add(new Rect(P1A, P2A));
    }

    void mitadMitad(Mat entrada, Mat salida) {
        if (entrada.channels() > salida.channels()) {
            Imgproc.cvtColor(salida, salida, Imgproc.COLOR_GRAY2RGBA);

        }
        if (entrada.channels() < salida.channels())
            Imgproc.cvtColor(entrada, entrada, Imgproc.COLOR_GRAY2RGBA);
//Representar la entrada en la mitad izquierda
        Rect mitad_izquierda = new Rect();
        mitad_izquierda.x = 0;
        mitad_izquierda.y = 0;
        mitad_izquierda.height = entrada.height();
        mitad_izquierda.width = entrada.width() / 2;

        if (entrada_mitad_izquierda != null)
            entrada_mitad_izquierda.release();

        if (salida_mitad_izquierda != null)
            salida_mitad_izquierda.release();

        salida_mitad_izquierda = salida.submat(mitad_izquierda);
        entrada_mitad_izquierda = entrada.submat(mitad_izquierda);
        entrada_mitad_izquierda.copyTo(salida_mitad_izquierda);
    }

    // Velocidades En España Ordenadas en binario
    private final String[] velocidadesEspanya = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "110", "120"};

    /**
     * Calcula la relacion entre la distancia mínima y maxima del centro para descartar triángulos y otras figuras geométrica.
     * Si la relación es 1 a 1 aproximadamente, estamos ante un figura circular
     *
     * @param curr_blob
     * @return
     */
    private double minMaxDistanceRatio(MatOfPoint curr_blob) {
        Point Sum = new Point(0.0, 0.0);
        // Compute the center of gravity of ponts of contour
        Size sizeA = curr_blob.size();
        for (int i = 0; i < sizeA.height; i++)
            for (int j = 0; j < sizeA.width; j++) {
                double[] pp = curr_blob.get(i, j);
                Sum.x = Sum.x + pp[0];
                Sum.y = Sum.y + pp[1];
            }
        double number_of_contour_ponts = sizeA.width * sizeA.height;
        Sum.x /= number_of_contour_ponts;
        Sum.y /= number_of_contour_ponts;
        Point center = Sum;
        double minima = Double.MAX_VALUE;
        double maxima = Double.MIN_VALUE;
        for (int i = 0; i < sizeA.height; i++)
            for (int j = 0; j < sizeA.width; j++) {
                double[] pp = curr_blob.get(i, j);
                double distancia = Math.sqrt(Math.pow(pp[0] - center.x, 2) + Math.pow(pp[1] - center.y, 2));
                minima = (distancia < minima) ? distancia : minima;
                maxima = (distancia > maxima) ? distancia : maxima;
            }
        return maxima / minima;
    }

    Mat rotate(Mat src, int angle) {
        Mat dst = new Mat();
        Point pt = new Point(src.width() / 2.0, src.height() / 2.0);
        Mat r = Imgproc.getRotationMatrix2D(pt, (angle != -1) ? angle : 0, 1.0);
        Imgproc.warpAffine(src, dst, r, new Size(src.width(), src.height()));
        r.release();
        return dst;
    }
}
