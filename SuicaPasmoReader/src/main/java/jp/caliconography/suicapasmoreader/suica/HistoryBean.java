package jp.caliconography.suicapasmoreader.suica;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by abeharuhiko on 2013/12/20.
 */
public class HistoryBean implements Serializable{

    private Date processDate;
    private String entranceStation;
    private String exitStation;

    public Long getRemain() {
        return remain;
    }

    public void setRemain(Long remain) {
        this.remain = remain;
    }

    private Long remain;

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getEntranceStation() {
        return entranceStation;
    }

    public void setEntranceStation(String entranceStation) {
        this.entranceStation = entranceStation;
    }

    public String getExitStation() {
        return exitStation;
    }

    public void setExitStation(String exitStation) {
        this.exitStation = exitStation;
    }
}
