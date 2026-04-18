package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

public class CatBodyComponent implements Component {
    public static final ComponentMapper<CatBodyComponent> MAPPER = ComponentMapper.getFor(CatBodyComponent.class);
    public Array<Vector2> bodyParts = new Array<>();
    public Array<Body> bodySegmentBodies = new Array<>();
    public float segmentRadius = 0.25f;
    public float segmentSpacing = 0.5f;
    public int maxLength = 10;
    public Color color;
}
