package com.gaias.morphiprojection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.mongodb.morphia.query.Query;

/**
 *
 * @author ronoel
 */
public abstract class ProjectViewExecutor {

    /**
     *
     * @param name_project
     * @param classe
     * @param query
     */
    public static void projectQuery(final String name_project, Query query) {
        Set<String> fields = ProjectViewExecutor.getFieldNamesFromClass(name_project, query.getEntityClass());
        ProjectViewExecutor.cleanFields(fields).forEach(field -> query.project(field, true));
    }

    private static Set<String> getFieldNamesFromClass(final String name_project, Class<?> classe) {
        return ProjectViewExecutor.getFieldNamesFromClass(name_project, classe, "");
    }

    private static Set<String> getFieldNamesFromClass(final String name_project, Class<?> classe, String root) {

        Set<String> fieldNames = new HashSet<>();

        boolean saveRoot = true;

        for (Field field : classe.getDeclaredFields()) {

            if (field.isAnnotationPresent(ProjectView.class)) {

                ProjectView annot = field.getAnnotation(ProjectView.class);

                if (Arrays.asList(annot.name()).contains(name_project)) {
                    saveRoot = false;

                    if (field.getType().isPrimitive()) {
                        fieldNames.add(root.length() > 0 ? (root + "." + field.getName()) : field.getName());
                    } else {
                        if (field.getGenericType() instanceof ParameterizedType) {
                            ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
                            Type[] arguments = collectionType.getActualTypeArguments();

                            // para map
                            for (int i = 0; i < arguments.length; i++) {
                                fieldNames.addAll(ProjectViewExecutor.getFieldNamesFromClass(name_project, (Class<?>) arguments[i], root.length() > 0 ? (root + "." + field.getName()) : field.getName()));
                            }
                        } else {
                            try {
                                Class<?> deepClass = Class.forName(field.getType().getName());
                                fieldNames.addAll(ProjectViewExecutor.getFieldNamesFromClass(name_project, deepClass, root.length() > 0 ? (root + "." + field.getName()) : field.getName()));
                            } catch (ClassNotFoundException ex) {
                                //  DO NOTHING -  Logger.getLogger(ProjectViewExecutor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }

            }
        }

        if (saveRoot && root != null && root.length() > 0) {
            fieldNames.add(root);
        }

        // Verifica super classes
        Class<?> superClasse = classe.getSuperclass();
        if (superClasse != null) {
            fieldNames.addAll(ProjectViewExecutor.getFieldNamesFromClass(name_project, superClasse, root));
        }

        return fieldNames;
    }

    private static Set<String> cleanFields(Set<String> fields) {

        Set<String> cleanedFields = new HashSet<>(fields);

        fields.forEach((field) -> {
            for (int i = 0; i < field.length(); i++) {
                if (i == field.lastIndexOf(".", i)) {
                    cleanedFields.remove(field.substring(0, i));
                }
            }
        });
        return cleanedFields;
    }
}
