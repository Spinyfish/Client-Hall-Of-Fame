/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TimeList<T> {
    private final LinkedHashMap<Long, T> map = new LinkedHashMap<>();
    private final long keepTime;
    public boolean autoUpdate = true;

    public TimeList(long keepTime) {
        this.keepTime = keepTime;
    }

    public void add(T value) {
        long t = keepTime + System.currentTimeMillis();
        if (autoUpdate) update();
        while (map.containsKey(t)) ++t;
        map.put(t, value);
    }

    public T getNearestAlive(long time) {
        long current = System.currentTimeMillis() + keepTime - time;
        long nearest = Long.MAX_VALUE;
        T value = null;
        if (autoUpdate) update();
        for (Map.Entry<Long, T> entry : map.entrySet()) {
            long dif = current - entry.getKey();
            if (dif >= 0 && dif <= nearest) {
                nearest = dif;
                value = entry.getValue();
            }
        }
        return value;
    }

    public int size() {
        if (autoUpdate) update();
        return map.size();
    }

    public void update() {
        final long current = System.currentTimeMillis();
        final Long[] waiting = new Long[map.size()];
        int index = 0;
        for (Map.Entry<Long, T> entry : map.entrySet()) {
            Long t = entry.getKey();
            if (t < current) {
                waiting[index] = t;
                ++index;
            }
        }
        for (Long l : waiting) {
            map.remove(l);
        }
    }

    public void clear() {
        map.clear();
    }
}
