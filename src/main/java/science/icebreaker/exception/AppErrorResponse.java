package science.icebreaker.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppErrorResponse {

    public Date timestamp;
    public int status;
    public String error;
    public List<String> errors;
}
