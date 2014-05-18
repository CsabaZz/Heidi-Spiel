package ch.szederkenyi.heidi.data.entities;

public class Question extends BaseEntity {
    /**
     * 
     */
    private static final long serialVersionUID = 7427589797238818176L;
    
    public String questionText;
    public String answer1;
    public String answer2;
    public String answer3;
    public String goodAnswer;

    public String questionImage;
    public String placeholder;
    
    public String goodImage;
    public String badImage;
}
