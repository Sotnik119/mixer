package com.sotnik.mixer;

/**
 * Константы
 */
public class Consts {

    /**
     * Количество миллисекунд для шага обновления громкости
     */
    public static int FADE_STEP_INTERVAL = 200;


    /**
     * Коды запросов для 1го и 2го трека
     */
    public class FileRequestType {
        public final static int FIRST_TRACK = 111;
        public final static int SECOND_TRACK = 222;
    }


    /**
     * Тип фейда
     */
    public class FadeMode {
        /**
         * Возрастание
         */
        public final static int FADE_IN = 333;
        /**
         * Затухание
         */
        public final static int FADE_OUT = 444;
    }

}
