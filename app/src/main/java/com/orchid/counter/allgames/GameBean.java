package com.orchid.counter.allgames;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by orchid on 2018/3/16.
 */

@Entity
public class GameBean {
    @Id(autoincrement = true)
    private Long    id;
    @Property(nameInDb = "PackageName")
    private String PackageName;
    @Generated(hash = 947771080)
    public GameBean(Long id, String PackageName) {
        this.id = id;
        this.PackageName = PackageName;
    }
    @Generated(hash = 1942203655)
    public GameBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPackageName() {
        return this.PackageName;
    }
    public void setPackageName(String PackageName) {
        this.PackageName = PackageName;
    }



}
