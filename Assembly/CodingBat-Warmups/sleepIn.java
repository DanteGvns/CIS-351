public boolean sleepIn(boolean weekday, boolean vacation) {
    if (!weekday) {
        return true;
    }
    if (vacation) {
        return true;
    }
    return false;
}
