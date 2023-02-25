public enum AddressPostFixType {

    DO("도"),
    SI("시"),
    GUN("군"),
    GU("구"),
    RO("로"),
    GIL("길")
    ;

    public final String postfix;

    AddressPostFixType(final String description) {
        this.postfix = description;
    }

}
