package com.linked_sys.hns.core;

import com.linked_sys.hns.Model.DaysInfo;
import com.linked_sys.hns.Model.LecInfo;
import com.linked_sys.hns.Model.Lessons;
import com.linked_sys.hns.Model.MailBody;
import com.linked_sys.hns.Model.SchoolStages;
import com.linked_sys.hns.Model.Son;
import com.linked_sys.hns.Model.Staff;
import com.linked_sys.hns.Model.Student;
import com.linked_sys.hns.Model.Teacher;
import com.linked_sys.hns.Model.TeacherClasses;
import com.linked_sys.hns.Model.TimeTable;
import com.linked_sys.hns.Model.WeekPlan;
import com.linked_sys.hns.Model.WeekPlanContent;
import com.linked_sys.hns.Model.Weeks;

import java.util.ArrayList;
import java.util.HashMap;

public class CacheHelper {
    private static CacheHelper mInstance = null;

    public static CacheHelper getInstance() {
        if (mInstance == null)
            mInstance = new CacheHelper();
        return mInstance;
    }

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    //USER TOKEN
    public String token = "";

    //STORE USER DATA
    public HashMap<String, String> userData = new HashMap<>();
    public ArrayList<Son> sonData = new ArrayList<>();
    public HashMap<String, String> selectedSon = new HashMap<>();
    public static int selectedSonID = 0;

    //TIME TABLE
    public ArrayList<DaysInfo> daysInfo = new ArrayList<>();
    public ArrayList<LecInfo> lecInfo = new ArrayList<>();
    public ArrayList<ArrayList<String>> lectures = new ArrayList<>();
    public ArrayList<ArrayList<String>> classes = new ArrayList<>();
    public ArrayList<TimeTable> timeTableList = new ArrayList<>();

    //WEEK PLAN
    public ArrayList<DaysInfo> planDaysInfo = new ArrayList<>();
    public ArrayList<LecInfo> planLecInfo = new ArrayList<>();
    public ArrayList<ArrayList<WeekPlan>> planLectures = new ArrayList<>();
    public ArrayList<WeekPlan> planWeekPlanList = new ArrayList<>();
    public ArrayList<Weeks> weeksList = new ArrayList<>();
    public Weeks currWeek;
    public int currWeekID;
    public WeekPlanContent content;

    //MESSAGES
    public MailBody body;
    public static int newMails;
    public ArrayList<Teacher> teachersList = new ArrayList<>();
    public ArrayList<Staff> staffList = new ArrayList<>();
    public boolean isNewMsg = true;
    public boolean isReply = false;
    public boolean isForward = false;
    public ArrayList<TeacherClasses> teacherClassesList = new ArrayList<>();
    public ArrayList<SchoolStages> stagesList = new ArrayList<>();
    public ArrayList<Student> studentList = new ArrayList<>();

    //TASKS
    public static int newTasks;

    //KEYS
    public static final String USER_ID_KEY = "UserID";
    public static final String USERNAME_1_KEY = "UserName1";
    public static final String USERNAME_2_KEY = "UserName2";
    public static final String USERNAME_3_KEY = "UserName3";
    public static final String USERNAME_4_KEY = "UserName4";
    public static final String USER_IMAGE_KEY = "UserImage";
    public static final String USER_TYPE_KEY = "UserType";
    public static final String TERM_ID_KEY = "TermID";
    public static final String SCHOOL_ID_KEY = "SchoolID";
    public static final String CLASS_ID_KEY = "ClassID";
    public static final String CLASS_NAME_KEY = "ClassName";
    public static final String STAGE_ID_KEY = "StageID";
    public static final String STAGE_NAME_KEY = "StageName";
    public static final String KEY_SENDER_ID = "SenderID";
    public static final String KEY_RECEIVERS = "Receivers";
    public static final String KEY_SUBJECT = "Subject";
    public static final String KEY_BODY = "MailBody";

    public int skills_100;
    public int skills_90_100;
    public int skills_80_90;
    public int skills_80;

    public static int activities;
    public static int absence;
    public static int behaviour;

    public ArrayList<Lessons> lessonsList = new ArrayList<>();

}
