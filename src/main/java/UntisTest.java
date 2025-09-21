import org.bytedream.untis4j.Session;
import org.bytedream.untis4j.LoginException;
import java.io.IOException;
import java.time.LocalDate;

public class UntisTest {
    // Flag für erweiterte Berechtigungen
    private static final boolean HAS_EXTENDED_PERMISSIONS = true;

    public static void main(String[] args) {
        try {
            // Login
            Session session = Session.login("aaron.heptin", "Koalabaer22!",
                    "https://hepta.webuntis.com", "fwm-ges-bielefeld");

            System.out.println("✅ Login erfolgreich!");
            System.out.println("Schulname: " + session.getInfos().getSchoolName());
            System.out.println("PersonId: " + session.getInfos().getPersonId());
            System.out.println("Klassen-ID: " + session.getInfos().getClassId());

            System.out.println("\n📅 === STUNDENPLAN ===");

            // Verschiedene Ansätze basierend auf Berechtigungen
            LocalDate today = LocalDate.now();


            getFullTimetable(session, today);


            session.logout();
            System.out.println("✅ Test erfolgreich abgeschlossen!");

        } catch (LoginException e) {
            System.out.println("❌ Login fehlgeschlagen: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("❌ IO-Fehler: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Unerwarteter Fehler: " + e.getMessage());
        }
    }

    private static void getFullTimetable(Session session, LocalDate today) throws IOException {
        System.out.println("Verwende vollständigen Stundenplan...");

        var resp = session.getWeeklyTimetableFromPersonId(
                today, session.getInfos().getPersonId()
        );

        System.out.println("Anzahl Stunden diese Woche: " + resp.size());

        var timetable = session.getTimetableFromPersonId(
                today, today, session.getInfos().getPersonId()
        );

        System.out.println("Anzahl Stunden: " + timetable.size());

        for (int i = 0; i < timetable.size(); i++) {
            var lesson = timetable.get(i);
            System.out.println("--- Stunde " + (i + 1) + " ---");
            System.out.println("Zeit: " + lesson.getStartTime() + " - " + lesson.getEndTime());
            System.out.println("Fach: " + lesson.getSubjects());
            //System.out.println("Lehrer: " + lesson.getTeachers());
            System.out.println("Raum: " + lesson.getRooms());
            System.out.println();
        }
    }


    private static String formatTime(int time) {
        String timeStr = String.valueOf(time);
        while (timeStr.length() < 4) {
            timeStr = "0" + timeStr;
        }
        return timeStr.substring(0, 2) + ":" + timeStr.substring(2);
    }

    private static String formatDate(int date) {
        String dateStr = String.valueOf(date);
        return dateStr.substring(6, 8) + "." +
                dateStr.substring(4, 6) + "." +
                dateStr.substring(0, 4);
    }
}