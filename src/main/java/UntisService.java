import org.bytedream.untis4j.Session;
import org.bytedream.untis4j.LoginException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.bytedream.untis4j.Session;
import org.bytedream.untis4j.LoginException;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class UntisService {

    public Map<String, Object> getTimetableData(String username, String password, String server, String schoolName, Integer days) {
        Map<String, Object> result = new HashMap<>();
        try {
            // Login
            // Login (basierend auf Ihrem UntisTest Code)
            Session session = Session.login(username, password, server, schoolName);

            result.put("success", true);
            result.put("schoolname", session.getInfos().getSchoolName());
            result.put("personId", session.getInfos().getPersonId());
            result.put("classId", session.getInfos().getClassId());

            // Basis-Informationen (wie in Ihrem Code)
            try {
                var classes = session.getClasses();
                result.put("totalClasses", classes.size());
            } catch (Exception e) {
                result.put("totalClasses", "Keine Berechtigung");
            }

            // Stundenplan abrufen
            LocalDate today = LocalDate.now();
            LocalDate endDate = (days != null && days > 1) ? today.plusDays(days - 1) : today;

            Map<String, Object> timetableData = getFullTimetable(session, today, endDate);
            result.put("timetable", timetableData);

            session.logout();
            System.out.println("✅ Test erfolgreich abgeschlossen!");

        } catch (LoginException e) {
            result.put("success", false);
            result.put("error", "Login fehlgeschlagen: " + e.getMessage());
        } catch (IOException e) {
            result.put("success", false);
            result.put("error", "IO-Fehler: " + e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Unerwarteter Fehler: " + e.getMessage());
        }
        return result;
    }

    private Map<String, Object> getFullTimetable(Session session, LocalDate start, LocalDate end) {
        Map<String, Object> timetableData = new HashMap<>();

        try {
            // Zuerst versuchen: Persönlicher Stundenplan (wie in Ihrem Code)
            var personalTimetable = session.getTimetableFromPersonId(start, end, session.getInfos().getPersonId());

            timetableData.put("method", "personal");
            timetableData.put("count", personalTimetable.size());
            timetableData.put("period", start + " bis " + end);

            List<Map<String, Object>> lessons = new ArrayList<>();

            for (int i = 0; i < personalTimetable.size(); i++) {
                var lesson = personalTimetable.get(i);
                Map<String, Object> lessonMap = new HashMap<>();

                lessonMap.put("date", lesson.getDate().toString());
                lessonMap.put("startTime", lesson.getStartTime().toString());
                lessonMap.put("endTime", lesson.getEndTime().toString());
                lessonMap.put("subjects", lesson.getSubjects().toString());
                lessonMap.put("rooms", lesson.getRooms().toString());
                lessonMap.put("classes", lesson.getClasses().toString());

                // Lehrer nur wenn verfügbar (Problem aus Ihrem ursprünglichen Code)
                try {
                    lessonMap.put("teachers", lesson.getTeachers().toString());
                } catch (Exception e) {
                    lessonMap.put("teachers", "Keine Berechtigung für Lehrer-Daten");
                }

                lessons.add(lessonMap);
            }

            timetableData.put("lessons", lessons);

        } catch (Exception e) {
            // Fallback: Custom Request (wie Sie es in Ihrem Debug-Code hatten)
            try {
                var customResponse = session.getCustomData("getTimetable", Map.of(
                        "options", Map.of(
                                "startDate", start.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                                "endDate", end.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                                "element", Map.of(
                                        "type", 5, // PERSON
                                        "id", session.getInfos().getPersonId()
                                ),
                                "onlyBaseTimetable", true,
                                "showInfo", false
                        )
                ));

                timetableData.put("method", "custom");
                if (!customResponse.isError()) {
                    var jsonArray = customResponse.getResponse().getJSONArray("result");
                    timetableData.put("count", jsonArray.length());
                    timetableData.put("rawData", jsonArray.toString());
                } else {
                    timetableData.put("error", "Custom request failed: " + customResponse.getErrorMessage());
                }

            } catch (Exception e2) {
                timetableData.put("error", "Beide Methoden fehlgeschlagen: " + e.getMessage() + " | " + e2.getMessage());
            }
        }

        return timetableData;
    }



}