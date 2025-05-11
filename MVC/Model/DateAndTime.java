package Model;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DateAndTime {
    public static ComboBox<LocalTime> TimePicker() {
        ComboBox<LocalTime> timePicker = new ComboBox<>();
        timePicker.setEditable(true);
        timePicker.setPrefWidth(120);

        // Initialize with the current time
        LocalTime now = LocalTime.now();
        LocalTime roundedTime = LocalTime.of(now.getHour(), now.getMinute() / 30 * 30);
        timePicker.setValue(roundedTime);

        // Add some common time options as suggestions
        List<LocalTime> timeList = new ArrayList<>();
        LocalTime time = LocalTime.of(0, 0); // Start at midnight
        for (int i = 0; i < 48; i++) { // 48 half-hour intervals = 24 hours
            timeList.add(time);
            time = time.plusMinutes(30);
        }
        timePicker.getItems().addAll(timeList);

        // Set a cell factory to format the display of time values in the dropdown
        timePicker.setCellFactory(lv -> new ListCell<LocalTime>() {
            @Override
            protected void updateItem(LocalTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                } else {
                    setText(time.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
            }
        });

        // Set a string converter to handle the text input and convert to/from LocalTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        timePicker.setConverter(new StringConverter<LocalTime>() {
            @Override
            public String toString(LocalTime time) {
                if (time == null) {
                    return "";
                }
                return formatter.format(time);
            }

            @Override
            public LocalTime fromString(String text) {
                if (text == null || text.isEmpty()) {
                    return null;
                }

                try {
                    // Try to parse the exact format first
                    return LocalTime.parse(text, formatter);
                } catch (DateTimeParseException e) {
                    // If that fails, try various common formats
                    try {
                        // Try with different patterns
                        String[] patterns = {
                                "H:mm",    // Single digit hour (1:30)
                                "h:mm a",  // 12-hour with AM/PM (1:30 PM)
                                "ha",      // Hour with AM/PM (1PM)
                                "H",       // Hour only (14)
                                "Hmm"      // No separator (1430)
                        };

                        for (String pattern : patterns) {
                            try {
                                return LocalTime.parse(text, DateTimeFormatter.ofPattern(pattern));
                            } catch (DateTimeParseException ex) {
                                // Continue trying other patterns
                            }
                        }

                        // Try handling special cases
                        text = text.toLowerCase().trim();
                        if (text.equals("noon") || text.equals("12pm")) {
                            return LocalTime.NOON;
                        } else if (text.equals("midnight") || text.equals("12am")) {
                            return LocalTime.MIDNIGHT;
                        }

                        // Last resort: try to extract hours and minutes from input
                        if (text.matches("\\d{1,2}")) {
                            // Just an hour value
                            int hour = Integer.parseInt(text);
                            if (hour >= 0 && hour <= 23) {
                                return LocalTime.of(hour, 0);
                            }
                        }

                        // If all parsing attempts fail, return null
                        return null;
                    } catch (Exception ex) {
                        return null;
                    }
                }
            }
        });

        // Add focus listener to format the input properly when focus is lost
        timePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // focus lost
                LocalTime value = timePicker.getValue();
                if (value != null) {
                    Platform.runLater(() -> {
                        // Re-set the value to trigger the converter's toString method
                        timePicker.setValue(value);
                    });
                }
            }
        });

        // Add an action listener to handle Enter key presses
        timePicker.getEditor().setOnAction(event -> {
            String text = timePicker.getEditor().getText();
            LocalTime time1 = timePicker.getConverter().fromString(text);
            if (time1 != null) {
                timePicker.setValue(time1);
            }
        });

        // Add a key released listener to update the value as the user types
        timePicker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                LocalTime time2 = timePicker.getConverter().fromString(newValue);
                if (time2 != null) {
                    // Update the value directly from the editor text
                    timePicker.setValue(time2);
                }
            } catch (Exception e) {
                // Ignore conversion errors during typing
            }
        });

        return timePicker;
    }


}
