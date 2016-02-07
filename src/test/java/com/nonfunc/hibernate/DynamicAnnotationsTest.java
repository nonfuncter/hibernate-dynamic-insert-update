package com.nonfunc.hibernate;

/*
 * #%L
 * em
 * %%
 * Copyright (C) 2016 nonfunc.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.airhacks.rulz.em.EntityManagerProvider.persistenceUnit;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;

import com.airhacks.rulz.em.EntityManagerProvider;

public class DynamicAnnotationsTest {

	private static Logger logger = Logger.getLogger(DynamicAnnotationsTest.class.getName());
	private static EntityManager entityManager;
	private static EntityTransaction transaction;

	@ClassRule
	public static EntityManagerProvider provider = persistenceUnit("hibernate-dynamic");

	@Rule
	public Stopwatch stopwatch = new Stopwatch() {
		@Override
		protected void finished(long nanos, Description description) {
			log(description.getMethodName(), nanos);
		}
	};

	@BeforeClass
	public static void setUp() throws Exception {
		entityManager = provider.em();
		transaction = provider.tx();
		transaction.begin();

		entityManager.flush();
		entityManager.clear();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		transaction.rollback();
	}

	@Before
	public void before() {
		entityManager.clear();
	}

	@Test
	public void testDefaultInsertUpdate() {	
		
		Foo foo = new Foo();
		foo.setCode(1);
		foo.setDescription("Static description");
		foo.setBar(Bar.TYPE2);
		entityManager.persist(foo);
		entityManager.flush();
		
		foo.setCode(2);
		entityManager.merge(foo);
		entityManager.flush();		
	}
	
	@Test
	public void testDynamicInsertUpdate() {		
		DynamicFoo foo = new DynamicFoo();
		foo.setCode(1);
		foo.setDescription("Static description");
		foo.setBar(Bar.TYPE2);
		entityManager.persist(foo);
		entityManager.flush();
		
		foo.setCode(2);
		entityManager.merge(foo);
		entityManager.flush();	
	}
		
	private static void log(String test, long nanos) {
		logger.info(String.format("Test %s took %d milliseconds.", test, TimeUnit.NANOSECONDS.toMillis(nanos)));
	}
}
