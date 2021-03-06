(ns cljgl.common.colors
  (:refer-clojure :exclude [get])
  (:require [clojure.string :as str]))

(def colors
  ; See https://www.w3schools.com/cssref/css_colors.asp + ":font-black for recommended font color"
  {:aliceblue            (list 240 248 255)
   :antiquewhite         (list 250 235 215)
   :aqua                 (list 0 255 255)
   :aquamarine           (list 127 255 212)
   :azure                (list 240 255 255)
   :beige                (list 245 245 220)
   :bisque               (list 255 228 196)
   :black                (list 0 0 0)
   :blanchedalmond       (list 255 235 205)
   :blue                 (list 0 0 255)
   :blueviolet           (list 138 43 226)
   :brown                (list 165 42 42)
   :burlywood            (list 222 184 135)
   :cadetblue            (list 95 158 160)
   :chartreuse           (list 127 255 0)
   :chocolate            (list 210 105 30)
   :coral                (list 255 127 80)
   :cornflowerblue       (list 100 149 237)
   :cornsilk             (list 255 248 220)
   :crimson              (list 220 20 60)
   :cyan                 (list 0 255 255)
   :darkblue             (list 0 0 139)
   :darkcyan             (list 0 139 139)
   :darkgoldenrod        (list 184 134 11)
   :darkgray             (list 169 169 169)
   :darkgreen            (list 0 100 0)
   :darkgrey             (list 169 169 169)
   :darkkhaki            (list 189 183 107)
   :darkmagenta          (list 139 0 139)
   :darkolivegreen       (list 85 107 47)
   :darkorange           (list 255 140 0)
   :darkorchid           (list 153 50 204)
   :darkred              (list 139 0 0)
   :darksalmon           (list 233 150 122)
   :darkseagreen         (list 143 188 143)
   :darkslateblue        (list 72 61 139)
   :darkslategray        (list 47 79 79)
   :darkslategrey        (list 47 79 79)
   :darkturquoise        (list 0 206 209)
   :darkviolet           (list 148 0 211)
   :deeppink             (list 255 20 147)
   :deepskyblue          (list 0 191 255)
   :dimgray              (list 105 105 105)
   :dimgrey              (list 105 105 105)
   :dodgerblue           (list 30 144 255)
   :firebrick            (list 178 34 34)
   :floralwhite          (list 255 250 240)
   :fontblack            (list 26 27 31)
   :forestgreen          (list 34 139 34)
   :fuchsia              (list 255 0 255)
   :gainsboro            (list 220 220 220)
   :ghostwhite           (list 248 248 255)
   :gold                 (list 255 215 0)
   :goldenrod            (list 218 165 32)
   :gray                 (list 128 128 128)
   :green                (list 0 128 0)
   :greenyellow          (list 173 255 47)
   :grey                 (list 128 128 128)
   :honeydew             (list 240 255 240)
   :hotpink              (list 173 255 47)
   :indianred            (list 205 92 92)
   :indigo               (list 75 0 130)
   :ivory                (list 255 255 240)
   :khaki                (list 240 230 140)
   :lavender             (list 230 230 250)
   :lavenderblush        (list 255 240 245)
   :lawngreen            (list 124 252 0)
   :lemonchiffon         (list 255 250 205)
   :lightblue            (list 173 216 230)
   :lightcoral           (list 240 128 128)
   :lightcyan            (list 224 255 255)
   :lightgoldenrodyellow (list 250 250 210)
   :lightgray            (list 211 211 211)
   :lightgreen           (list 144 238 144)
   :lightgrey            (list 211 211 211)
   :lightpink            (list 255 182 193)
   :lightsalmon          (list 255 160 122)
   :lightseagreen        (list 32 178 170)
   :lightskyblue         (list 135 206 250)
   :lightslategray       (list 119 136 153)
   :lightslategrey       (list 119 136 153)
   :lightsteelblue       (list 176 196 222)
   :lightyellow          (list 255 255 224)
   :lime                 (list 0 255 0)
   :limegreen            (list 50 205 50)
   :linen                (list 250 240 230)
   :magenta              (list 255 0 255)
   :maroon               (list 128 0 0)
   :mediumaquamarine     (list 102 205 170)
   :mediumblue           (list 0 0 205)
   :mediumorchid         (list 186 85 211)
   :mediumpurple         (list 147 112 219)
   :mediumseagreen       (list 60 179 113)
   :mediumslateblue      (list 123 104 238)
   :mediumspringgreen    (list 0 250 154)
   :mediumturquoise      (list 72 209 204)
   :mediumvioletred      (list 199 21 133)
   :midnightblue         (list 25 25 112)
   :mintcream            (list 245 255 250)
   :mistyrose            (list 255 228 225)
   :moccasin             (list 255 228 181)
   :navajowhite          (list 255 222 173)
   :navy                 (list 0 0 128)
   :oldlace              (list 253 245 230)
   :olive                (list 128 128 0)
   :olivedrab            (list 107 142 35)
   :orange               (list 255 165 0)
   :orangered            (list 255 69 0)
   :orchid               (list 218 112 214)
   :palegoldenrod        (list 238 232 170)
   :palegreen            (list 152 251 152)
   :paleturquoise        (list 175 238 238)
   :palevioletred        (list 219 112 147)
   :papayawhip           (list 255 239 213)
   :peachpuff            (list 255 218 185)
   :peru                 (list 205 133 63)
   :pink                 (list 255 192 203)
   :plum                 (list 221 160 221)
   :powderblue           (list 176 224 230)
   :purple               (list 128 0 128)
   :rebeccapurple        (list 102 51 153)
   :red                  (list 255 0 0)
   :rosybrown            (list 188 143 143)
   :royalblue            (list 65 105 225)
   :saddlebrown          (list 139 69 19)
   :salmon               (list 250 128 114)
   :sandybrown           (list 244 164 96)
   :seagreen             (list 46 139 87)
   :seashell             (list 255 245 238)
   :sienna               (list 160 82 45)
   :silver               (list 192 192 192)
   :skyblue              (list 135 206 235)
   :slateblue            (list 106 90 205)
   :slategray            (list 112 128 144)
   :slategrey            (list 112 128 144)
   :snow                 (list 255 250 250)
   :springgreen          (list 0 255 127)
   :steelblue            (list 70 130 180)
   :tan                  (list 210 180 140)
   :teal                 (list 0 128 128)
   :thistle              (list 216 191 216)
   :tomato               (list 255 99 71)
   :turquoise            (list 64 224 208)
   :violet               (list 238 130 238)
   :wheat                (list 245 222 179)
   :white                (list 255 255 255)
   :whitesmoke           (list 245 245 245)
   :yellow               (list 255 255 0)
   :yellowgreen          (list 154 205 50)})

(defn get [color-keyword]
  (->> color-keyword name
       (#(str/replace % #"\-" ""))
       str/lower-case
       keyword
       (clojure.core/get colors)))

(defn get-raw
  "Gets a color directly, expects in format with no dashes, underscores or uppercase."
  [color-keyword]
  (clojure.core/get colors color-keyword))