package ${entity.javaPackage}.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import ${entity.javaPackage}.${entity.className};
import ${schema.appPackage}.view.base.EntityBindable;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

// KEEP INCLUDES - put your custom includes here
<#if keepIncludes?has_content>${keepIncludes!}</#if>// KEEP INCLUDES END

/**
 * Created by yinzara on 8/13/15.
 */
@EViewGroup
public class ${entity.className}ListItemView extends LinearLayout implements EntityBindable<${entity.className}> {

    protected ${entity.className} ${entity.fieldName?uncap_first};

    //////////
    ///Inject views by Id
    ///////
    // KEEP FIELDS - put your custom fields here
${keepFields!}    // KEEP FIELDS END

    public ${entity.className}ListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void bind(${entity.className} entity) {
        this.${entity.fieldName?uncap_first} = entity;
        ////////Bind entity to views
        // KEEP BODY - put your custom methods here
${keepBody!}        // KEEP BODY END
    }

    // KEEP METHODS - put your custom methods here
    ${keepMethods!}// KEEP METHODS END

}
