package jp.caliconography.suicapasmoreader.suica;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by abeharuhiko on 2013/12/20.
 */
public class HistoryBean implements Serializable{

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    private String processType;
    private Date processDate;
    private String entranceStation;
    private String exitStation;
    private Long remain;
    private boolean isProductSales;

    public boolean isProductSales() {
        return isProductSales;
    }

    public void setProductSales(boolean isProductSales) {
        this.isProductSales = isProductSales;
    }

    public Long getRemain() {
        return remain;
    }

    public void setRemain(Long remain) {
        this.remain = remain;
    }


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
