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

package com.liferay.media.object.apio.internal.architect.resource.test;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.liferay.apio.architect.file.BinaryFile;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.resource.NestedCollectionResource;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.media.object.apio.architect.model.MediaObject;
import com.liferay.portal.apio.test.util.PaginationRequest;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.io.ByteArrayInputStream;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class DefaultMediaObjectNestedCollectionResourceTest
	extends BaseMediaObjectNestedCollectionResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddMediaObject() throws Exception {
		String content = RandomTestUtil.randomString(10);

		FileEntry fileEntry = _addFileEntry(
			_group.getGroupId(),
			new MediaObjectImpl(
				new BinaryFile(
					new ByteArrayInputStream(content.getBytes(UTF_8)), 0L,
					"application/octet-stream"),
				"My media object testAddMediaObject",
				"My media object description", null, null));

		Assert.assertNotNull(fileEntry.getCreateDate());
		Assert.assertEquals(
			"My media object description", fileEntry.getDescription());
		Assert.assertNotNull(fileEntry.getExtension());
		Assert.assertNotNull(fileEntry.getMimeType());
		Assert.assertNotNull(fileEntry.getModifiedDate());
		Assert.assertEquals(fileEntry.getSize(), 0L);
		Assert.assertEquals(
			"My media object testAddMediaObject", fileEntry.getTitle());
	}

	@Test
	public void testGetFileEntryPreviewURL() throws Exception {
		String content = RandomTestUtil.randomString(10);

		FileEntry fileEntry = _addFileEntry(
			_group.getGroupId(),
			new MediaObjectImpl(
				new BinaryFile(
					new ByteArrayInputStream(content.getBytes(UTF_8)), 0L,
					"application/octet-stream"),
				"My media object testGetFileEntryPreviewURL", null, null,
				null));

		String fileEntryPreview = _getFileEntryPreviewURL(fileEntry);

		Assert.assertNotNull(fileEntryPreview);
	}

	@Test
	public void testGetMediaObjectAssetTags() throws Exception {
		String content = RandomTestUtil.randomString(10);

		List<String> keywords = Arrays.asList("keyword1", "keyword2");

		FileEntry fileEntry = _addFileEntry(
			_group.getGroupId(),
			new MediaObjectImpl(
				new BinaryFile(
					new ByteArrayInputStream(content.getBytes(UTF_8)), 0L,
					"application/octet-stream"),
				"My media object testAddMediaObject", null, keywords, null));

		List<String> mediaObjectAssetTags = _getMediaObjectAssetTags(fileEntry);

		Assert.assertEquals(
			mediaObjectAssetTags.toString(), 2, mediaObjectAssetTags.size());

		Assert.assertTrue(
			mediaObjectAssetTags.toString(),
			mediaObjectAssetTags.containsAll(keywords));
	}

	@Test
	public void testGetPageItems() throws Exception {
		String content = RandomTestUtil.randomString(10);

		_addFileEntry(
			_group.getGroupId(),
			new MediaObjectImpl(
				new BinaryFile(
					new ByteArrayInputStream(content.getBytes(UTF_8)), 0L,
					"application/octet-stream"),
				"My media object testGetPageItems",
				"My media object description", null, null));

		PageItems<FileEntry> pageItems = getPageItems(
			PaginationRequest.of(10, 1), _group.getGroupId());

		Assert.assertEquals(1, pageItems.getTotalCount());

		List<FileEntry> fileEntries = (List<FileEntry>)pageItems.getItems();

		Assert.assertEquals(fileEntries.toString(), 1, fileEntries.size());

		FileEntry fileEntry = fileEntries.get(0);

		Assert.assertEquals(
			"My media object description", fileEntry.getDescription());
		Assert.assertEquals(
			"My media object testGetPageItems", fileEntry.getTitle());
	}

	private FileEntry _addFileEntry(long groupId, MediaObject mediaObject)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_addFileEntry", long.class, MediaObject.class);

		method.setAccessible(true);

		return (FileEntry)method.invoke(
			getNestedCollectionResource(), groupId, mediaObject);
	}

	private String _getFileEntryPreviewURL(FileEntry fileEntry)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_getFileEntryPreviewURL", FileEntry.class);

		method.setAccessible(true);

		return (String)method.invoke(getNestedCollectionResource(), fileEntry);
	}

	private List<String> _getMediaObjectAssetTags(FileEntry fileEntry)
		throws Exception {

		NestedCollectionResource nestedCollectionResource =
			getNestedCollectionResource();

		Class<?> clazz = nestedCollectionResource.getClass();

		Method method = clazz.getDeclaredMethod(
			"_getMediaObjectAssetTags", FileEntry.class);

		method.setAccessible(true);

		return (List<String>)method.invoke(
			getNestedCollectionResource(), fileEntry);
	}

	@DeleteAfterTestRun
	private Group _group;

	private static class MediaObjectImpl implements MediaObject {

		@Override
		public BinaryFile getBinaryFile() {
			return _binaryFile;
		}

		@Override
		public List<Long> getCategories() {
			return _categories;
		}

		@Override
		public String getDescription() {
			return _description;
		}

		@Override
		public List<String> getKeywords() {
			return _keywords;
		}

		@Override
		public String getTitle() {
			return _title;
		}

		private MediaObjectImpl(
			BinaryFile binaryFile, String title, String description,
			List<String> keywords, List<Long> categories) {

			_binaryFile = binaryFile;
			_title = title;
			_description = description;
			_keywords = Optional.ofNullable(
				keywords
			).map(
				List::stream
			).orElseGet(
				Stream::empty
			).collect(
				Collectors.toList()
			);
			_categories = Optional.ofNullable(
				categories
			).map(
				List::stream
			).orElseGet(
				Stream::empty
			).collect(
				Collectors.toList()
			);
		}

		private final BinaryFile _binaryFile;
		private final List<Long> _categories;
		private final String _description;
		private final List<String> _keywords;
		private final String _title;

	}

}