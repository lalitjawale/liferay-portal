/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.friendly.url.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.friendly.url.exception.DuplicateFriendlyURLException;
import com.liferay.friendly.url.exception.FriendlyURLLengthException;
import com.liferay.friendly.url.model.FriendlyURL;
import com.liferay.friendly.url.service.FriendlyURLLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class FriendlyURLLocalServiceTest {

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetUniqueUrlTitleNormalizesUrlTitle() throws Exception {
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);

		String urlTitle = "url title with spaces";

		String uniqueUrlTitle = FriendlyURLLocalServiceUtil.getUniqueUrlTitle(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			TestPropsValues.getUserId(), urlTitle);

		Assert.assertEquals("url-title-with-spaces", uniqueUrlTitle);
	}

	@Test
	public void testGetUniqueUrlTitleResolvesConflicts() throws Exception {
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);

		String urlTitle = "existing-url-title";

		FriendlyURLLocalServiceUtil.addFriendlyURL(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			TestPropsValues.getUserId(), urlTitle);

		String uniqueUrlTitle = FriendlyURLLocalServiceUtil.getUniqueUrlTitle(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			_user.getUserId(), urlTitle);

		Assert.assertEquals("existing-url-title-1", uniqueUrlTitle);
	}

	@Test
	public void testGetUniqueUrlTitleReusesOwnedUrlTitles() throws Exception {
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);

		String urlTitle = "existing-url-title";

		FriendlyURLLocalServiceUtil.addFriendlyURL(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			TestPropsValues.getUserId(), urlTitle);

		String uniqueUrlTitle = FriendlyURLLocalServiceUtil.getUniqueUrlTitle(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			TestPropsValues.getUserId(), urlTitle);

		Assert.assertEquals(urlTitle, uniqueUrlTitle);
	}

	@Test
	public void testGetUniqueUrlTitleShortensToMaxLength() throws Exception {
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURL.class.getName(), "urlTitle");

		String urlTitle = StringUtil.randomString(maxLength + 1);

		String uniqueUrlTitle = FriendlyURLLocalServiceUtil.getUniqueUrlTitle(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			TestPropsValues.getUserId(), urlTitle);

		Assert.assertEquals(maxLength, uniqueUrlTitle.length());
	}

	@Test(expected = DuplicateFriendlyURLException.class)
	public void testValidateNonUniqueUrlTitle() throws Exception {
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURL.class.getName(), "urlTitle");

		String urlTitle = StringUtil.randomString(maxLength);

		FriendlyURLLocalServiceUtil.addFriendlyURL(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			TestPropsValues.getUserId(), urlTitle);

		FriendlyURLLocalServiceUtil.validate(
			_group.getCompanyId(), _group.getGroupId(), classNameId, urlTitle);
	}

	@Test(expected = DuplicateFriendlyURLException.class)
	public void testValidateUrlTitleNotOwnedByModel() throws Exception {
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURL.class.getName(), "urlTitle");

		String urlTitle = StringUtil.randomString(maxLength);

		FriendlyURLLocalServiceUtil.addFriendlyURL(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			TestPropsValues.getUserId(), urlTitle);

		FriendlyURLLocalServiceUtil.validate(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			_user.getUserId(), urlTitle);
	}

	@Test
	public void testValidateUrlTitleOwnedByModel() throws Exception {
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURL.class.getName(), "urlTitle");

		String urlTitle = StringUtil.randomString(maxLength);

		FriendlyURLLocalServiceUtil.addFriendlyURL(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			TestPropsValues.getUserId(), urlTitle);

		FriendlyURLLocalServiceUtil.validate(
			_group.getCompanyId(), _group.getGroupId(), classNameId,
			TestPropsValues.getUserId(), urlTitle);
	}

	@Test(expected = FriendlyURLLengthException.class)
	public void testValidateUrlTitleWithInvalidLength() throws Exception {
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURL.class.getName(), "urlTitle");

		String urlTitle = StringUtil.randomString(maxLength + 1);

		FriendlyURLLocalServiceUtil.validate(
			_group.getCompanyId(), _group.getGroupId(), classNameId, urlTitle);
	}

	@Test
	public void testValidateUrlTitleWithMaxLength() throws Exception {
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURL.class.getName(), "urlTitle");

		String urlTitle = StringUtil.randomString(maxLength);

		FriendlyURLLocalServiceUtil.validate(
			_group.getCompanyId(), _group.getGroupId(), classNameId, urlTitle);
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _user;

}