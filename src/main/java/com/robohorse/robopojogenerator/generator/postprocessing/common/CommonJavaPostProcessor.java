package com.robohorse.robopojogenerator.generator.postprocessing.common;

import com.robohorse.robopojogenerator.generator.common.ClassField;
import com.robohorse.robopojogenerator.generator.common.ClassItem;
import com.robohorse.robopojogenerator.generator.consts.annotations.LombokAnnotations;
import com.robohorse.robopojogenerator.generator.consts.templates.ClassTemplate;
import com.robohorse.robopojogenerator.generator.consts.templates.ImportsTemplate;
import com.robohorse.robopojogenerator.models.FieldModel;
import com.robohorse.robopojogenerator.models.GenerationModel;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by vadim on 25.09.16.
 */
public class CommonJavaPostProcessor extends JavaPostProcessor {
    @Inject
    public CommonJavaPostProcessor() {
    }

    @Override
    public String proceedClassBody(ClassItem classItem, GenerationModel generationModel) {
        final StringBuilder classBodyBuilder = new StringBuilder();
        final StringBuilder classMethodBuilder = new StringBuilder();

        final Map<String, ClassField> classFields = classItem.getClassFields();

        for (String objectName : classFields.keySet()) {
            final String classItemValue = classFields.get(objectName).getJavaItem();
            final String itemNameFormatted = generateHelper.formatClassField(objectName);
            classBodyBuilder.append(classTemplateHelper.createFiled(
                    new FieldModel
                            .Builder()
                            .setClassType(classItemValue)
                            .setFieldNameFormatted(itemNameFormatted)
                            .setFieldName(objectName)
                            .setAnnotation(classItem.getAnnotation())
                            .build()
            ));
            if (generationModel.isUseSetters()) {
                if (!generationModel.isUseLombok()) {
                    classMethodBuilder.append(ClassTemplate.NEW_LINE);
                    classMethodBuilder.append(classTemplateHelper.createSetter(
                            itemNameFormatted,
                            classItemValue));

                } else {
                    classItem.addClassImport(ImportsTemplate.SETTER);
                    generateHelper.addClassAnnotation(classItem, LombokAnnotations.AT_SETTER);
                }
            }
            if (generationModel.isUseGetters()) {
                if (!generationModel.isUseLombok()) {
                    classMethodBuilder.append(ClassTemplate.NEW_LINE);
                    classMethodBuilder.append(classTemplateHelper.createGetter(
                            itemNameFormatted,
                            classItemValue));
                } else {
                    classItem.addClassImport(ImportsTemplate.GETTER);
                    generateHelper.addClassAnnotation(classItem, LombokAnnotations.AT_GETTER);
                }
            }
        }
        if (generationModel.isUseStrings()) {
            if (!generationModel.isUseLombok()) {
                classMethodBuilder.append(ClassTemplate.NEW_LINE);
                classMethodBuilder.append(classTemplateHelper.createToString(
                        classItem
                ));

            } else {
                classItem.addClassImport(ImportsTemplate.TO_STRING);
                generateHelper.addClassAnnotation(classItem, LombokAnnotations.AT_TOSTRING);
            }
        }
        classBodyBuilder.append(classMethodBuilder);
        return classBodyBuilder.toString();
    }

    @Override
    public String createClassTemplate(ClassItem classItem, String classBody) {
        return classTemplateHelper.createClassBody(classItem, classBody);
    }
}
