package jp.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author pj_zhong
 * @email pj_zhong@163.com / pj_zhong@gmail.com
 */

/*
*  文章查询条件类
* */
public class ArticleQuery {
    /*Search by title*/
    private String title;
    /*Search by state*/
    private String state;

    /*Search by tag ids*/
    private List<Integer> tagIds;
    private Integer[] listIds;
    private Integer categoryId;


    public ArticleQuery() {
        title = null;
        tagIds = null;
        categoryId = -1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public List<Integer> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Integer> tagIds) {
        this.tagIds = tagIds;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addTagId(Integer id) {
        if(id > 0){

            if(tagIds==null){
                tagIds = new ArrayList<Integer>();
            }

            tagIds.add(id);
        }
    }

    public Integer[] getListIds() {
        return listIds;
    }

    public void setListIds(Integer[] listIds) {
        this.listIds = listIds;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ArticleQuery{");
        sb.append("title='").append(title).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", tagIds=").append(tagIds);
        sb.append(", listIds=").append(Arrays.toString(listIds));
        sb.append(", categoryId=").append(categoryId);
        sb.append('}');
        return sb.toString();
    }
}
