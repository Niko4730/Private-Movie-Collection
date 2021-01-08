package BE;

import BLL.CategoryManager;
import javafx.beans.property.*;

public class Category {

    private int categoryId;
    private String categoryName;
    private StringProperty categoryNameProperty;
    protected SimpleDoubleProperty categoryDurationProperty;
    protected SimpleStringProperty categoryDurationStringProperty;
    private ObjectProperty<Integer> categorySize = new SimpleObjectProperty<>();

    public void setCategorySize(Integer categorySize) {
        this.categorySize.set(categorySize);
    }

    /**
     * Constructor with categoryName
     *
     * @param categoryName
     */
    public Category(String categoryName) {
        initialize();
        setCategoryName(categoryName);
    }

    /**
     * Constructor with id and categoryName
     *
     * @param id
     * @param categoryName
     */
    public Category(int id, String categoryName) {
        initialize();
        setCategoryId(id);
        setCategoryName(categoryName);
    }

    /**
     * Constructor with id, category name and total duration.
     * @param id
     * @param categoryName
     * @param totalDuration
     */
    public Category(int id, String categoryName, double totalDuration) {
        initialize();
        setCategoryId(id);
        setCategoryName(categoryName);
        setCategoryDurationProperty(totalDuration);
        setCategoryDurationStringProperty(totalDuration);
    }

    /**
     * initializes the variables
     */
    private void initialize() {
        categoryNameProperty = new SimpleStringProperty();
        categoryDurationProperty = new SimpleDoubleProperty();
        categoryDurationStringProperty = new SimpleStringProperty();
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Get the value of name property
     *
     * @return the value of name property
     */
    public StringProperty getcategoryNameProperty() {
        return categoryNameProperty;
    }

    /**
     * Set the value of name.
     *
     * @param categoryName new value of name
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        this.categoryNameProperty.setValue(categoryName);
    }

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Set the value of id
     *
     * @param categoryId new value of id
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Get the category duration string property.
     * @return
     */
    public SimpleStringProperty categoryDurationPropertyString() {
        return categoryDurationStringProperty;
    }

    /**
     * Set the category string property. Time gets automatically formatted.
     * @param duration
     */
    public void setCategoryDurationStringProperty(double duration) {
        var min = (int) duration;
        var sec = (int) duration / 60;
        String str = String.format("%d:%02d", min, sec);
        categoryDurationStringProperty.set(str);
    }

    /**
     * Get the category duration property.
     * @return
     */
    public SimpleDoubleProperty categoryDurationProperty() {
        return categoryDurationProperty;
    }

    /**
     * Set the category duration property.
     * @param duration
     */
    public void setCategoryDurationProperty(double duration) {
        categoryDurationProperty.set(duration);
    }

    /**
     * Get the total duration of the category.
     * @return
     */
    public double getTotalDuration() {
        return categoryDurationProperty.get();
    }

    /**
     * Get the formatted total duration of the category for GUI.
     * @return
     */
    public String getTotalDurationString() {
        return categoryDurationStringProperty.get();
    }

    /**
     * Get the total amount of movies in the category.
     * @return
     */
    public ObjectProperty<Integer> getCategorySize() {
        CategoryManager categoryManager = new CategoryManager();
        try {
            categorySize.setValue(categoryManager.loadMoviesInCategory(this.getCategoryId()).size());
            return categorySize;
        } catch (Exception e) {
            e.printStackTrace();
            return new SimpleObjectProperty<>(0);
        }
    }
}
