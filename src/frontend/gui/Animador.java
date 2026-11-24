package frontend.gui;

import java.util.function.IntConsumer;
import javax.swing.Timer;

/** Avanza paso a paso sobre una lista de visitados ya calculada (javax.swing.Timer, en el EDT). */
final class Animador {

    private final Timer timer;
    private final IntConsumer alPaso;
    private final Runnable alTerminar;
    private int total;
    private int paso;
    private int pasosPorTick = 1;

    Animador(IntConsumer alPaso, Runnable alTerminar) {
        this.alPaso = alPaso;
        this.alTerminar = alTerminar;
        this.timer = new Timer(30, e -> alPulso());
    }

    /** Velocidad de 1 (lenta) a 10 (rapida). */
    void setVelocidad(int v) {
        timer.setDelay(Math.max(8, 1000 / (v * v)));
    }

    void iniciar(int total) {
        timer.stop();
        this.total = total;
        this.paso = 0;
        // Para mapas grandes se avanzan varios pasos por tick y la animacion no se eterniza.
        this.pasosPorTick = Math.max(1, total / 300);
        alPaso.accept(0);
        if (total > 0) {
            timer.start();
        } else {
            alTerminar.run();
        }
    }

    boolean corriendo() {
        return timer.isRunning();
    }

    boolean terminada() {
        return paso >= total;
    }

    void pausar() {
        timer.stop();
    }

    void continuar() {
        if (!terminada()) {
            timer.start();
        }
    }

    void siguiente() {
        timer.stop();
        avanzar(1);
    }

    void saltarAlFinal() {
        timer.stop();
        avanzar(total - paso);
    }

    void detener() {
        timer.stop();
        total = 0;
        paso = 0;
    }

    private void alPulso() {
        avanzar(pasosPorTick);
    }

    private void avanzar(int n) {
        if (paso >= total) {
            return;
        }
        paso = Math.min(total, paso + n);
        alPaso.accept(paso);
        if (paso >= total) {
            timer.stop();
            alTerminar.run();
        }
    }
}
