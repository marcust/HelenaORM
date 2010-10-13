/*
 * The MIT License
 *
 * Copyright (c) 2010 Marcus Thiesen (marcus@thiesen.org)
 *
 * This file is part of HelenaORM.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.thiesen.helenaorm;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.dao.Command;
import me.prettyprint.cassandra.model.HectorException;
import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.Keyspace;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.commons.beanutils.PropertyUtils;
import org.thiesen.helenaorm.annotations.ColumnProperty;
import org.thiesen.helenaorm.annotations.HelenaColumnBean;
import org.thiesen.helenaorm.annotations.KeyProperty;
import org.thiesen.helenaorm.annotations.ValueProperty;

import com.google.common.collect.ImmutableMap;

public class HelenaColumnDAO<T> {

//	private final Class<T> clz;
	private final TypeConverter typeConverter;

	private final PropertyDescriptor[] propertyDescriptors;
	private Map<String, Field> fields;

	private final String hostname;
	private final int port;
	private String[] nodes = null;

	private final String keyspace;
	private final String columnFamily;
	private final String secondaryColumnFamily;

	private ConsistencyLevel consistencyLevel = CassandraClient.DEFAULT_CONSISTENCY_LEVEL;

	private PropertyDescriptor keyPropertyDescriptor;
	private PropertyDescriptor columnPropertyDescriptor;
	private PropertyDescriptor valuePropertyDescriptor;

	public HelenaColumnDAO(final Class<T> clz, final String hostname,
			final int port, final SerializeUnknownClasses serializationPolicy,
			final ImmutableMap<Class<?>, TypeMapping<?>> typeMappings) {

		if (!clz.isAnnotationPresent(HelenaColumnBean.class)) {
			throw new IllegalArgumentException(
					"Trying to get a HelenaColumnDAO for a class that is not mapped with @HelenaColumnBean");
		}

		final HelenaColumnBean annotation = clz
				.getAnnotation(HelenaColumnBean.class);
		this.typeConverter = new TypeConverter(typeMappings, serializationPolicy);
//		this.clz = clz;

		this.keyspace = annotation.keyspace();
		this.columnFamily = annotation.columnFamily();
		this.secondaryColumnFamily = annotation.secondaryColumnFamily();

		this.hostname = hostname;
		this.port = port;

		if (annotation.consistency() != null) {
			this.setConsistencyLevel(annotation.consistency());
		}

		this.fields = new HashMap<String, Field>();
		for (Field field : clz.getDeclaredFields()) {
			this.fields.put(field.getName(), field);
		}

		this.propertyDescriptors = PropertyUtils.getPropertyDescriptors(clz);
		for (final PropertyDescriptor descriptor : propertyDescriptors) {
			if (isKeyProperty(descriptor)) {
				this.keyPropertyDescriptor = descriptor;
			} else if (isColumnProperty(descriptor)) {
				this.columnPropertyDescriptor = descriptor;
			} else if (isValueProperty(descriptor)) {
				this.valuePropertyDescriptor = descriptor;
			}
		}

		if (keyPropertyDescriptor == null) {
			throw new HelenaRuntimeException("Could not find key of class "
					+ clz.getName() + ", did you annotate with @KeyProperty");
		}

		if (columnPropertyDescriptor == null) {
			throw new HelenaRuntimeException("Could not find column of class "
					+ clz.getName() + ", did you annotate with @ColumnProperty");
		}
	}

	public HelenaColumnDAO(final Class<T> clz, final String[] nodes,
			final SerializeUnknownClasses serializationPolicy,
			final ImmutableMap<Class<?>, TypeMapping<?>> typeMappings) {

		this(clz, null, 0, serializationPolicy, typeMappings);
		this.nodes = nodes;
	}

	public void insert(final T object) {
		try {
			final String keyName = keyPropertyDescriptor.getName();
			final Object keyValue = PropertyUtils.getProperty(object, keyName);
			final byte[] keyBytes = typeConverter.convertValueObjectToByteArray(keyValue);

			final String columnName = columnPropertyDescriptor.getName();
			final Object columnValue = PropertyUtils.getProperty(object, columnName);
			final byte[] columnBytes = typeConverter.convertValueObjectToByteArray(columnValue);

			byte[] tempValue = null;
			if (valuePropertyDescriptor != null) {
				final String valueName = valuePropertyDescriptor.getName();
				final Object valueValue = PropertyUtils.getProperty(object, valueName);
				tempValue = typeConverter.convertValueObjectToByteArray(valueValue);
			} else {
				final long timestamp = System.currentTimeMillis();
				tempValue = typeConverter.convertValueObjectToByteArray(timestamp);
			}
			final byte[] value = tempValue;

			execute(new Command<Void>() {
				@Override
				public Void execute(final Keyspace ks) throws HectorException {
					
					ColumnPath columnPath = new ColumnPath();
					columnPath.setColumn_family(columnFamily);
					columnPath.setColumn(columnBytes);
					ks.insert(keyValue.toString(), columnPath, value);
					
					if (secondaryColumnFamily != null && !secondaryColumnFamily.isEmpty()) {
						columnPath = new ColumnPath();
						columnPath.setColumn_family(secondaryColumnFamily);
						columnPath.setColumn(keyBytes);
						ks.insert(columnValue.toString(), columnPath, value);
					}
					
					return null;
				}
			});

		} catch (final Exception e) {
			throw new HelenaRuntimeException(e);
		}
	}

	public void delete(final T object) {
		try {
			final String keyName = keyPropertyDescriptor.getName();
			final Object keyValue = PropertyUtils.getProperty(object, keyName);
			final byte[] keyBytes = typeConverter.convertValueObjectToByteArray(keyValue);

			final String columnName = columnPropertyDescriptor.getName();
			final Object columnValue = PropertyUtils.getProperty(object, columnName);
			final byte[] columnBytes = typeConverter.convertValueObjectToByteArray(columnValue);

			execute(new Command<Void>() {
				@Override
				public Void execute(final Keyspace ks) throws HectorException {
					
					ColumnPath columnPath = new ColumnPath();
					columnPath.setColumn_family(columnFamily);
					columnPath.setColumn(columnBytes);
					ks.remove(keyValue.toString(), columnPath);
					
					if (secondaryColumnFamily != null && !secondaryColumnFamily.isEmpty()) {
						columnPath = new ColumnPath();
						columnPath.setColumn_family(secondaryColumnFamily);
						columnPath.setColumn(keyBytes);
						ks.remove(columnValue.toString(), columnPath);
					}
					
					return null;
				}
			});

		} catch (final Exception e) {
			throw new HelenaRuntimeException(e);
		}
	}

	private boolean isKeyProperty(final PropertyDescriptor d) {
		return safeIsAnnotationPresent(d, KeyProperty.class);
	}

	private boolean isColumnProperty(final PropertyDescriptor d) {
		return safeIsAnnotationPresent(d, ColumnProperty.class);
	}

	private boolean isValueProperty(final PropertyDescriptor d) {
		return safeIsAnnotationPresent(d, ValueProperty.class);
	}

	private boolean safeIsAnnotationPresent(final PropertyDescriptor d,
			final Class<? extends Annotation> annotation) {
		return nullSafeAnnotationPresent(annotation, fields.get(d.getName()))
				|| nullSafeAnnotationPresent(annotation, d.getReadMethod())
				|| nullSafeAnnotationPresent(annotation, d.getWriteMethod());
	}

	private boolean nullSafeAnnotationPresent(
			final Class<? extends Annotation> annotation, final Method method) {
		if (method != null) {
			if (method.isAnnotationPresent(annotation)) {
				return true;
			}
		}
		return false;
	}

	private boolean nullSafeAnnotationPresent(
			final Class<? extends Annotation> annotation, final Field field) {
		return (field != null && field.isAnnotationPresent(annotation));
	}

	public List<String> getColumns(final String key) {
		final List<String> result = new ArrayList<String>();
		try {
			execute(new Command<Void>() {
				@Override
				public Void execute(final Keyspace ks) throws HectorException {
					
					SlicePredicate predicate = new SlicePredicate();
					SliceRange sliceRange = new SliceRange();
			        sliceRange.setStart(new byte[] {});
			        sliceRange.setFinish(new byte[] {});
			        predicate.setSlice_range(sliceRange);
			        
					List<Column> list = ks.getSlice(
							key, new ColumnParent(columnFamily), predicate);
					
					if (list != null && !list.isEmpty()) {
						String name = null;
						for (Column column : list) {
							name = typeConverter.bytesToString(column.getName());
							result.add(name);
						}
					}
					return null;
				}
			});
		} catch (final Exception e) {
			throw new HelenaRuntimeException(e);
		}
		return result;
	}

	public List<String> getColumnsBySecondary(final String key) {
		final List<String> result = new ArrayList<String>();
		try {
			execute(new Command<Void>() {
				@Override
				public Void execute(final Keyspace ks) throws HectorException {
					
					SlicePredicate predicate = new SlicePredicate();
					SliceRange sliceRange = new SliceRange();
			        sliceRange.setStart(new byte[] {});
			        sliceRange.setFinish(new byte[] {});
			        predicate.setSlice_range(sliceRange);
			        
					List<Column> list = ks.getSlice(
							key, new ColumnParent(secondaryColumnFamily), predicate);
					
					if (list != null && !list.isEmpty()) {
						String name = null;
						for (Column column : list) {
							name = typeConverter.bytesToString(column.getName());
							result.add(name);
						}
					}
					return null;
				}
			});
		} catch (final Exception e) {
			throw new HelenaRuntimeException(e);
		}
		return result;
	}

	public List<String> getValues(final String key) {
		final List<String> result = new ArrayList<String>();
		try {
			execute(new Command<Void>() {
				@Override
				public Void execute(final Keyspace ks) throws HectorException {
					
					SlicePredicate predicate = new SlicePredicate();
					SliceRange sliceRange = new SliceRange();
			        sliceRange.setStart(new byte[] {});
			        sliceRange.setFinish(new byte[] {});
			        predicate.setSlice_range(sliceRange);
			        
					List<Column> list = ks.getSlice(
							key, new ColumnParent(columnFamily), predicate);
					
					if (list != null && !list.isEmpty()) {
						String value = null;
						for (Column column : list) {
							value = typeConverter.bytesToString(column.getValue());
							result.add(value);
						}
					}
					return null;
				}
			});
		} catch (final Exception e) {
			throw new HelenaRuntimeException(e);
		}
		return result;
	}

	private <V> V execute(final Command<V> command) throws Exception {
		if (hostname != null) {
			return command.execute(hostname, port, keyspace);
		} else if (nodes != null) {
			return command.execute(nodes, keyspace, consistencyLevel);
		} else {
			throw new HelenaRuntimeException(
					"One of these must be set: hostname and port or an array of nodes.");
		}
	}

	public ConsistencyLevel getConsistencyLevel() {
		return consistencyLevel;
	}

	public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
		this.consistencyLevel = consistencyLevel;
	}

}
