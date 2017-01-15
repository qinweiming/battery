package models;

import models.api.Jsonable;

import java.time.Instant;

/**
 * Created by zhaoxy on 15/01/2017.
 */
public class Timestamp implements Jsonable{
    public long seconds;
    public int nanos;
    public Timestamp(){
        Instant now = Instant.now();
        seconds= now.getEpochSecond();
        nanos =now.getNano();

    }
    public static final Timestamp now(){
        return new Timestamp();
    }
    public Timestamp(long seconds, int nanos) {
        this.seconds = seconds;
        this.nanos = nanos;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public int getNanos() {
        return nanos;
    }

    public void setNanos(int nanos) {
        this.nanos = nanos;
    }
}
