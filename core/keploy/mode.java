package keploy;

public class mode {

    private ModeType Mode;

    public mode() {

    }

    public mode(ModeType mode) {
        Mode = mode;
    }

    public ModeType getMode() {
        return Mode;
    }

    public void setMode(ModeType mode) {
        Mode = mode;
    }

    public boolean isValid(ModeType mode) {
        if (mode == ModeType.MODE_RECORD || mode == ModeType.MODE_TEST || mode == ModeType.MODE_OFF) {
            return true;
        }
        return false;
    }

    public void setTestMode() {
        setMode(ModeType.MODE_TEST);
    }

//    public ModeType getModeFromContext(Context ctx){
//        return ModeType.MODE_OFF;
//    }

    public enum ModeType {
        MODE_RECORD,
        MODE_TEST,
        MODE_OFF;

        public String getTypeName() {
            switch (this) {
                case MODE_RECORD:
                    return "record";
                case MODE_TEST:
                    return "test";
                case MODE_OFF:
                    return "off";
            }
            return "unknown";
        }
    }
}
