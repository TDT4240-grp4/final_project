package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class CatBodyComponent implements Component {
    public static final ComponentMapper<CatBodyComponent> MAPPER = ComponentMapper.getFor(CatBodyComponent.class);

    public Array<Vector2> bodyParts = new Array<>();
    public Color color = new Color();
    public float segmentRadius = 0.25f;
    public int maxLength = 10; // The number of body segments
    public float segmentSpacing = 0.1f; // Distance between segments

    private float distanceMoved = 0;
}
