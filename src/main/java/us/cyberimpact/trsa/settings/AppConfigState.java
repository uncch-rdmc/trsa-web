package us.cyberimpact.trsa.settings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 *
 * @author asone
 */
public class AppConfigState {
    private static final Logger logger = Logger.getLogger(AppConfigState.class.getName());
    
    static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public AppConfigState(String state, String solution) {
        this.state = state;
        this.solution = solution;
        LocalDateTime now = LocalDateTime.now();
        this.reportTime = now.format(formatter);
    }

    public AppConfigState() {
        LocalDateTime now = LocalDateTime.now();
        this.reportTime = now.format(formatter);
    }
    
    
    
    
        private String state;

    /**
     * Get the value of state
     *
     * @return the value of state
     */
    public String getState() {
        return state;
    }

    /**
     * Set the value of state
     *
     * @param state new value of state
     */
    public void setState(String state) {
        this.state = state;
    }

    private String solution;

    /**
     * Get the value of solution
     *
     * @return the value of solution
     */
    public String getSolution() {
        return solution;
    }

    /**
     * Set the value of solution
     *
     * @param solution new value of solution
     */
    public void setSolution(String solution) {
        this.solution = solution;
    }

        private String reportTime;

    /**
     * Get the value of reportTime
     *
     * @return the value of reportTime
     */
    public String getReportTime() {
        return reportTime;
    }

    /**
     * Set the value of reportTime
     *
     * @param reportTime new value of reportTime
     */
    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    @Override
    public String toString() {
        return "AppConfigState{" + "state=" + state + ", solution=" + 
          solution + ", reportTime=" + reportTime + '}';
    }

    
    
    
}
