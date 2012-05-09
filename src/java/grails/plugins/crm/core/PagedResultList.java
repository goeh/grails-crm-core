/*
 * Copyright 2012 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.crm.core;

import java.util.List;
import java.util.AbstractList;

/**
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
public class PagedResultList<E> extends AbstractList<E> {

    private List<E> resultList;
    private int totalCount;

    public PagedResultList(List<E> list, int totalCount) {
        this.resultList = list;
        this.totalCount = totalCount;
    }

    public PagedResultList(List<E> list) {
        this.resultList = list;
        this.totalCount = list.size();
    }

    @Override
    public E get(int i) {
        return resultList.get(i);
    }

    @Override
    public E set(int i, E o) {
        return resultList.set(i, o);
    }

    @Override
    public E remove(int i) {
        return resultList.remove(i);
    }

    @Override
    public void add(int i, E o) {
        resultList.add(i, o);
    }

    @Override
    public int size() {
        return resultList.size();
    }

    public int getTotalCount() {
        return this.totalCount;
    }
}
