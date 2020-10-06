package science.icebreaker.exception;

import java.util.Date;
import java.util.List;

public class AppErrorResponse {

    private Date timestamp;
    private int status;
    private String error;
    private List<String> errors;

    public AppErrorResponse() {
    }

    public AppErrorResponse(Date timestamp, int status, String error, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.errors = errors;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppErrorResponse)) {
            return false;
        }

        AppErrorResponse that = (AppErrorResponse) o;

        if (getStatus() != that.getStatus()) {
            return false;
        }
        if (getTimestamp() != null
            ? !getTimestamp().equals(that.getTimestamp())
            : that.getTimestamp() != null
        ) {
            return false;
        }
        if (getError() != null
            ? !getError().equals(that.getError())
            : that.getError() != null
        ) {
            return false;
        }

        return getErrors() != null
            ? getErrors().equals(that.getErrors())
            : that.getErrors() == null;
    }

    @Override
    public int hashCode() {
        int result = getTimestamp() != null ? getTimestamp().hashCode() : 0;
        final int number = 31; // fix: what is 31?
        result = number * result + getStatus();
        result = number * result + (getError() != null ? getError().hashCode() : 0);
        result = number * result + (getErrors() != null ? getErrors().hashCode() : 0);
        return result;
    }
}
