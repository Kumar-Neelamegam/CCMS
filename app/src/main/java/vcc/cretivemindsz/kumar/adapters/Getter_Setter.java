package vcc.cretivemindsz.kumar.adapters;


/**
 * Created at 15/05/2017
 * Muthukumar N & Vidhya K
 */
public class Getter_Setter {

    //**********************************************************************************************

    public static class Dashboard_Dataobjects {

        public String Title_Name;
        int Icon;

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        int Id;

        public String getTitle_Name() {
            return Title_Name;
        }

        public void setTitle_Name(String title_Name) {
            Title_Name = title_Name;
        }

        public int getIcon() {
            return Icon;
        }

        public void setIcon(int icon) {
            Icon = icon;
        }


    }

    //**********************************************************************************************
    public static class Mstr_barcodes
    {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getSID() {
            return SID;
        }

        public void setSID(String SID) {
            this.SID = SID;
        }

        int id;
        String Name;
        String SID;

        public String getSID_PATH() {
            return SID_PATH;
        }

        public void setSID_PATH(String SID_PATH) {
            this.SID_PATH = SID_PATH;
        }

        String SID_PATH;


    }

    //**********************************************************************************************

    /**
     * Created at 23/05/2017
     * Muthukumar N & Vidhya K
     */
    //1. Mstr_Batch
    public static class Mstr_Batch
    {

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public String getBatch_Name() {
            return Batch_Name;
        }

        public void setBatch_Name(String batch_Name) {
            Batch_Name = batch_Name;
        }

        public String getYear() {
            return Year;
        }

        public void setYear(String year) {
            Year = year;
        }

        public String getCoaching_Days() {
            return Coaching_Days;
        }

        public void setCoaching_Days(String coaching_Days) {
            Coaching_Days = coaching_Days;
        }

        public String getClass_Timing() {
            return Class_Timing;
        }

        public void setClass_Timing(String class_Timing) {
            Class_Timing = class_Timing;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        int Id;

        public String getIsEdited() {
            return IsEdited;
        }

        public void setIsEdited(String isEdited) {
            IsEdited = isEdited;
        }

        public String getIsDeleted() {
            return IsDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            IsDeleted = isDeleted;
        }

        String IsEdited,IsDeleted;
        String Subject;

        public String getSubjectId() {
            return SubjectId;
        }

        public void setSubjectId(String subjectId) {
            SubjectId = subjectId;
        }

        String SubjectId;


        public String getSubject() {
            return Subject;
        }

        public void setSubject(String subject) {
            Subject = subject;
        }

        String Batch_Name,Year,Coaching_Days,Class_Timing,Status;

    }


    //**********************************************************************************************

    //2. Mstr_Fee

    public static class Mstr_Fee
    {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIsEdited() {
            return IsEdited;
        }

        public void setIsEdited(String isEdited) {
            IsEdited = isEdited;
        }

        public String getIsDeleted() {
            return IsDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            IsDeleted = isDeleted;
        }

        public String getSubject() {
            return Subject;
        }

        public void setSubject(String subject) {
            Subject = subject;
        }

        public String getFee() {
            return Fee;
        }

        public void setFee(String fee) {
            Fee = fee;
        }

        public String getIsActive() {
            return IsActive;
        }

        public void setIsActive(String isActive) {
            IsActive = isActive;
        }

        public String getSubjectId() {
            return SubjectId;
        }

        public void setSubjectId(String subjectId) {
            SubjectId = subjectId;
        }


        int id;
        String  IsEdited,IsDeleted;
        String Subject,Fee;
        String IsActive;
        String SubjectId;


    }

    //**********************************************************************************************
    //3. Mstr_Occupation
    public static class Mstr_Occupation
    {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIsEdited() {
            return IsEdited;
        }

        public void setIsEdited(String isEdited) {
            IsEdited = isEdited;
        }

        public String getIsDeleted() {
            return IsDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            IsDeleted = isDeleted;
        }

        public String getOccupation() {
            return Occupation;
        }

        public void setOccupation(String occupation) {
            Occupation = occupation;
        }

        public String getIsActive() {
            return IsActive;
        }

        public void setIsActive(String isActive) {
            IsActive = isActive;
        }

        int id; String IsEdited,IsDeleted;
        String Occupation;
        String IsActive;


    }

    //**********************************************************************************************
    //4. Mstr_School
    public static class Mstr_School
    {

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIsEdited() {
            return IsEdited;
        }

        public void setIsEdited(String isEdited) {
            IsEdited = isEdited;
        }

        public String getIsDeleted() {
            return IsDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            IsDeleted = isDeleted;
        }

        public String getSchool() {
            return School;
        }

        public void setSchool(String school) {
            School = school;
        }

        public String getIsActive() {
            return IsActive;
        }

        public void setIsActive(String isActive) {
            IsActive = isActive;
        }

        int id; String IsEdited,IsDeleted;
        String School;
        String IsActive;


    }

    //**********************************************************************************************
    public static class Mstr_Test
    {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIsEdited() {
            return IsEdited;
        }

        public void setIsEdited(String isEdited) {
            IsEdited = isEdited;
        }

        public String getIsDeleted() {
            return IsDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            IsDeleted = isDeleted;
        }

        public String getBatch() {
            return Batch;
        }

        public void setBatch(String batch) {
            Batch = batch;
        }

        public String getBatch_ID() {
            return Batch_ID;
        }

        public void setBatch_ID(String batch_ID) {
            Batch_ID = batch_ID;
        }

        public String getSubject() {
            return Subject;
        }

        public void setSubject(String subject) {
            Subject = subject;
        }

        public String getSubject_ID() {
            return Subject_ID;
        }

        public void setSubject_ID(String subject_ID) {
            Subject_ID = subject_ID;
        }

        public String getNameOfTest() {
            return NameOfTest;
        }

        public void setNameOfTest(String nameOfTest) {
            NameOfTest = nameOfTest;
        }

        public String getDateOfTest() {
            return DateOfTest;
        }

        public void setDateOfTest(String dateOfTest) {
            DateOfTest = dateOfTest;
        }

        public String getMaxMarks() {
            return MaxMarks;
        }

        public void setMaxMarks(String maxMarks) {
            MaxMarks = maxMarks;
        }

        public String getIsActive() {
            return IsActive;
        }

        public void setIsActive(String isActive) {
            IsActive = isActive;
        }

        int id; String IsEdited,IsDeleted;
        String Batch,Batch_ID,Subject,Subject_ID,NameOfTest,DateOfTest,MaxMarks;
        String IsActive;





    }
    //5. Mstr_Subject
    public static class Mstr_Subject
    {

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIsEdited() {
            return IsEdited;
        }

        public void setIsEdited(String isEdited) {
            IsEdited = isEdited;
        }

        public String getIsDeleted() {
            return IsDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            IsDeleted = isDeleted;
        }

        public String getSubject() {
            return Subject;
        }

        public void setSubject(String subject) {
            Subject = subject;
        }

        public String getHandledBy() {
            return HandledBy;
        }

        public void setHandledBy(String handledBy) {
            HandledBy = handledBy;
        }

        public String getIsActive() {
            return IsActive;
        }

        public void setIsActive(String isActive) {
            IsActive = isActive;
        }

        int id; String IsEdited,IsDeleted;
        String Subject,HandledBy;
        String IsActive;


    }
    //**********************************************************************************************
     //5. StudentItems
    public static class StudentItems
    {

        int id; String IsEdited,IsDeleted;
        String Photo,Name,SID,Batch,Barcode;
        String IsActive;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIsEdited() {
            return IsEdited;
        }

        public void setIsEdited(String isEdited) {
            IsEdited = isEdited;
        }

        public String getIsDeleted() {
            return IsDeleted;
        }

        public void setIsDeleted(String isDeleted) {
            IsDeleted = isDeleted;
        }

        public String getPhoto() {
            return Photo;
        }

        public void setPhoto(String photo) {
            Photo = photo;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getSID() {
            return SID;
        }

        public void setSID(String SID) {
            this.SID = SID;
        }

        public String getBatch() {
            return Batch;
        }

        public void setBatch(String batch) {
            Batch = batch;
        }

        public String getBarcode() {
            return Barcode;
        }

        public void setBarcode(String barcode) {
            Barcode = barcode;
        }

        public String getIsActive() {
            return IsActive;
        }

        public void setIsActive(String isActive) {
            IsActive = isActive;
        }
    }
    //**********************************************************************************************

    //Take attendance
    public static class TakeAttendance
    {
        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public String getSID() {
            return SID;
        }

        public void setSID(String SID) {
            this.SID = SID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        int Id;
        String SID,Name,Status;





    }

    //**********************************************************************************************

    public static class MarkEntry
    {

        int Id;
        String SID,Name,Marks;

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public String getSID() {
            return SID;
        }

        public void setSID(String SID) {
            this.SID = SID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getMarks() {
            return Marks;
        }

        public void setMarks(String marks) {
            Marks = marks;
        }
    }

    //**********************************************************************************************

    //**********************************************************************************************

    //**********************************************************************************************

    //**********************************************************************************************

    public class Pricing {

        String noOfstudent="0";
        String perStudent="0";
        String cardName="";
        String totalPrice="";
        String cardType="";
        String cardColor="";
        String payId;

        public String getCardColor() {
            return cardColor;
        }

        public void setCardColor(String cardColor) {
            this.cardColor = cardColor;
        }

        public String getPayId() {
            return payId;
        }

        public void setPayId(String payId) {
            this.payId = payId;
        }

        public Pricing(String noOfstudent, String perStudent, String cardName, String totalPrice, String cardType) {
            this.noOfstudent = noOfstudent;
            this.perStudent = perStudent;
            this.cardName = cardName;
            this.totalPrice = totalPrice;
            this.cardType = cardType;
        }

        public String getNoOfstudent() {
            return noOfstudent;
        }

        public void setNoOfstudent(String noOfstudent) {
            this.noOfstudent = noOfstudent;
        }

        public String getPerStudent() {
            return perStudent;
        }

        public void setPerStudent(String perStudent) {
            this.perStudent = perStudent;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }
    }
    //**********************************************************************************************


}//END
