/*
 * Copyright (C) 2011 Marta Spodymek
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.prometheuscode.xml.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation can be put over the method of class being
 * {@code @Convertable}.
 * </p>
 * <p>
 * It stays that the object returned by method should have its mapping
 * registered because containing class depends on it to full conversion.
 * <p>
 * If object returned is a mix of Containers, that is Maps or Lists,
 * 
 * <pre>
 * For ex.
 * {@code Map<String, Map<String, <List<User>>>>}
 * </pre>
 * 
 * then "dependee" object is Map VALUE or List element. In the example above
 * "dependee" object is *User* and its class should have {@code @Convertable} put
 * over declaration.
 * </p>
 * 
 * Also object class returned by annoted method should have {@code @Convertable}
 * put over it declaration if not, a runtime exception will be thrown. </p>
 * 
 * 
 * 
 * @author marta
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dependee {

}
