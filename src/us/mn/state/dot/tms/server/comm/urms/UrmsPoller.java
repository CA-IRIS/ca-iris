/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2010  Minnesota Department of Transportation
 * Copyright (C) 2010-2015  AHMCT, University of California
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.server.comm.urms;

import java.io.IOException;
import java.util.LinkedList;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.Operation;
import us.mn.state.dot.tms.server.comm.SamplePoller;
import us.mn.state.dot.tms.utils.TimedGate;

/**
 * Implementation of the Caltrans URMS VDS protocol using UDP.
 * An object of this class is instantiated once, per defined
 * CommLink using this protocol. This driver supports multiple
 * field devices forwarding UDP packets to the same port. The
 * sensor id field in each VDS station sample is used to match
 * the drop value specified for each controller in the comm link.
 * The poller reads UDP datagrams from the input stream continuously.
 * <p><p>
 * Packet format:<p>
 * <p>
 * <table border="1">
 * <tr><td>Byte index</td><td>Description</td></tr>
 * <tr><td>0</td><td>U [0x55]</td></tr>
 * <tr><td>1</td><td>R [0x52]</td></tr>
 * <tr><td>2</td><td>M [0x4D] (correction: use 0x40)</td></tr>
 * <tr><td>3</td><td>S [0x53]</td></tr>
 * <tr><td>4</td><td>2 [0x32]</td></tr>
 * <tr><td>5</td><td>Bytes to Checksum (MSB) [0x01]</td></tr>
 * <tr><td>6</td><td>Bytes to Checksum (LSB)  [0x9A]</td></tr>
 * <tr><td>7</td><td>Station ID Digit 4</td></tr>
 * <tr><td>8</td><td>Station ID Digit 3</td></tr>
 * <tr><td>9</td><td>Station ID Digit 2</td></tr>
 * <tr><td>10</td><td>Station ID Digit 1</td></tr>
 * <tr><td>11</td><td>Spare Byte 1 - Always set to zero</td></tr>
 * <tr><td>12</td><td>Spare Byte 2 - Always set to zero</td></tr>
 * <tr><td>13</td><td>Spare Byte 3 - Always set to zero</td></tr>
 * <tr><td>14</td><td>Day</td></tr>
 * <tr><td>15</td><td>Month</td></tr>
 * <tr><td>16</td><td>Year (MSB)</td></tr>
 * <tr><td>17</td><td>Year (LSB)</td></tr>
 * <tr><td>18</td><td>Hour</td></tr>
 * <tr><td>19</td><td>Minute</td></tr>
 * <tr><td>20</td><td>Second</td></tr>
 * <tr><td>21</td><td>Number of Metered Lanes</td></tr>
 * <tr><td>22</td><td>Number of Mainline Lanes</td></tr>
 * <tr><td>23</td><td>Number of Opposite Mainline Lanes</td></tr>
 * <tr><td>24</td><td>Number of Additional Detectors</td></tr>
 * <tr><td>25</td><td>Mainline 1 Speed</td></tr>
 * <tr><td>26</td><td>Mainline 1 Leading Volume</td></tr>
 * <tr><td>27</td><td>Mainline 1 Leading Occupancy (MSB)</td></tr>
 * <tr><td>28</td><td>Mainline 1 Leading Occupancy (LSB)</td></tr>
 * <tr><td>29</td><td>Mainline 1 Leading Status</td></tr>
 * <tr><td>30</td><td>Mainline 1 Trailing Volume</td></tr>
 * <tr><td>31</td><td>Mainline 1 Trailing Occupancy (MSB)</td></tr>
 * <tr><td>32</td><td>Mainline 1 Trailing Occupancy (LSB)</td></tr>
 * <tr><td>33</td><td>Mainline 1 Trailing Status</td></tr>
 * <tr><td>34</td><td>Mainline 1 Lane Status</td></tr>
 * <tr><td>35</td><td>Mainline 2 Speed</td></tr>
 * <tr><td>36</td><td>Mainline 2 Leading Volume</td></tr>
 * <tr><td>37</td><td>Mainline 2 Leading Occupancy (MSB)</td></tr>
 * <tr><td>38</td><td>Mainline 2 Leading Occupancy (LSB)</td></tr>
 * <tr><td>39</td><td>Mainline 2 Leading Status</td></tr>
 * <tr><td>40</td><td>Mainline 2 Trailing Volume</td></tr>
 * <tr><td>41</td><td>Mainline 2 Trailing Occupancy (MSB)</td></tr>
 * <tr><td>42</td><td>Mainline 2 Trailing Occupancy (LSB)</td></tr>
 * <tr><td>43</td><td>Mainline 2 Trailing Status</td></tr>
 * <tr><td>44</td><td>Mainline 2 Lane Status</td></tr>
 * <tr><td>45</td><td>Mainline 3 Speed</td></tr>
 * <tr><td>46</td><td>Mainline 3 Leading Volume</td></tr>
 * <tr><td>47</td><td>Mainline 3 Leading Occupancy (MSB)</td></tr>
 * <tr><td>48</td><td>Mainline 3 Leading Occupancy (LSB)</td></tr>
 * <tr><td>49</td><td>Mainline 3 Leading Status</td></tr>
 * <tr><td>50</td><td>Mainline 3 Trailing Volume</td></tr>
 * <tr><td>51</td><td>Mainline 3 Trailing Occupancy (MSB)</td></tr>
 * <tr><td>52</td><td>Mainline 3 Trailing Occupancy (LSB)</td></tr>
 * <tr><td>53</td><td>Mainline 3 Trailing Status</td></tr>
 * <tr><td>54</td><td>Mainline 3 Lane Status</td></tr>
 * <tr><td>55</td><td>Mainline 4 Speed</td></tr>
 * <tr><td>56</td><td>Mainline 4 Leading Volume</td></tr>
 * <tr><td>57</td><td>Mainline 4 Leading Occupancy (MSB)</td></tr>
 * <tr><td>58</td><td>Mainline 4 Leading Occupancy (LSB)</td></tr>
 * <tr><td>59</td><td>Mainline 4 Leading Status</td></tr>
 * <tr><td>60</td><td>Mainline 4 Trailing Volume</td></tr>
 * <tr><td>61</td><td>Mainline 4 Trailing Occupancy (MSB)</td></tr>
 * <tr><td>62</td><td>Mainline 4 Trailing Occupancy (LSB)</td></tr>
 * <tr><td>63</td><td>Mainline 4 Trailing Status</td></tr>
 * <tr><td>64</td><td>Mainline 4 Lane Status</td></tr>
 * <tr><td>65</td><td>Mainline 5 Speed</td></tr>
 * <tr><td>66</td><td>Mainline 5 Leading Volume</td></tr>
 * <tr><td>67</td><td>Mainline 5 Leading Occupancy (MSB)</td></tr>
 * <tr><td>68</td><td>Mainline 5 Leading Occupancy (LSB)</td></tr>
 * <tr><td>69</td><td>Mainline 5 Leading Status</td></tr>
 * <tr><td>70</td><td>Mainline 5 Trailing Volume</td></tr>
 * <tr><td>71</td><td>Mainline 5 Trailing Occupancy (MSB)</td></tr>
 * <tr><td>72</td><td>Mainline 5 Trailing Occupancy (LSB)</td></tr>
 * <tr><td>73</td><td>Mainline 5 Trailing Status</td></tr>
 * <tr><td>74</td><td>Mainline 5 Lane Status</td></tr>
 * <tr><td>75</td><td>Mainline 6 Speed</td></tr>
 * <tr><td>76</td><td>Mainline 6 Leading Volume</td></tr>
 * <tr><td>77</td><td>Mainline 6 Leading Occupancy (MSB)</td></tr>
 * <tr><td>78</td><td>Mainline 6 Leading Occupancy (LSB)</td></tr>
 * <tr><td>79</td><td>Mainline 6 Leading Status</td></tr>
 * <tr><td>80</td><td>Mainline 6 Trailing Volume</td></tr>
 * <tr><td>81</td><td>Mainline 6 Trailing Occupancy (MSB)</td></tr>
 * <tr><td>82</td><td>Mainline 6 Trailing Occupancy (LSB)</td></tr>
 * <tr><td>83</td><td>Mainline 6 Trailing Status</td></tr>
 * <tr><td>84</td><td>Mainline 6 Lane Status</td></tr>
 * <tr><td>85</td><td>Mainline 7 Speed</td></tr>
 * <tr><td>86</td><td>Mainline 7 Leading Volume</td></tr>
 * <tr><td>87</td><td>Mainline 7 Leading Occupancy (MSB)</td></tr>
 * <tr><td>88</td><td>Mainline 7 Leading Occupancy (LSB)</td></tr>
 * <tr><td>89</td><td>Mainline 7 Leading Status</td></tr>
 * <tr><td>90</td><td>Mainline 7 Trailing Volume</td></tr>
 * <tr><td>91</td><td>Mainline 7 Trailing Occupancy (MSB)</td></tr>
 * <tr><td>92</td><td>Mainline 7 Trailing Occupancy (LSB)</td></tr>
 * <tr><td>93</td><td>Mainline 7 Trailing Status</td></tr>
 * <tr><td>94</td><td>Mainline 7 Lane Status</td></tr>
 * <tr><td>95</td><td>Mainline 8 Speed</td></tr>
 * <tr><td>96</td><td>Mainline 8 Leading Volume</td></tr>
 * <tr><td>97</td><td>Mainline 8 Leading Occupancy (MSB)</td></tr>
 * <tr><td>98</td><td>Mainline 8 Leading Occupancy (LSB)</td></tr>
 * <tr><td>99</td><td>Mainline 8 Leading Status</td></tr>
 * <tr><td>100</td><td>Mainline 8 Trailing Volume</td></tr>
 * <tr><td>101</td><td>Mainline 8 Trailing Occupancy (MSB)</td></tr>
 * <tr><td>102</td><td>Mainline 8 Trailing Occupancy (LSB)</td></tr>
 * <tr><td>103</td><td>Mainline 8 Trailing Status</td></tr>
 * <tr><td>104</td><td>Mainline 8 Lane Status</td></tr>
 * <tr><td>105</td><td>Mainline Direction Bits<br> 
 *	(Each Lane 0=Normal, 1=Reverse)</td></tr>
 * <tr><td>106</td><td>Opposite MainLine 1 Speed</td></tr>
 * <tr><td>107</td><td>Opposite MainLine 1 Leading Volume</td></tr>
 * <tr><td>108</td><td>Opposite MainLine 1 Leading Occupancy(MSB)</td></tr>
 * <tr><td>109</td><td>Opposite MainLine 1 Leading Occupancy(LSB)</td></tr>
 * <tr><td>110</td><td>Opposite MainLine 1 Leading Status</td></tr>
 * <tr><td>111</td><td>Opposite MainLine 1 Trailing Volume</td></tr>
 * <tr><td>112</td><td>Opposite MainLine 1 Trailing Occupancy(MSB)</td></tr>
 * <tr><td>113</td><td>Opposite MainLine 1 Trailing Occupancy(LSB)</td></tr>
 * <tr><td>114</td><td>Opposite MainLine 1 Trailing Status</td></tr>
 * <tr><td>115</td><td>Opposite MainLine 1 Lane Status</td></tr>
 * <tr><td>116</td><td>Opposite MainLine 2 Speed</td></tr>
 * <tr><td>117</td><td>Opposite MainLine 2 Leading Volume</td></tr>
 * <tr><td>118</td><td>Opposite MainLine 2 Leading Occupancy(MSB)</td></tr>
 * <tr><td>119</td><td>Opposite MainLine 2 Leading Occupancy(LSB)</td></tr>
 * <tr><td>120</td><td>Opposite MainLine 2 Leading Status</td></tr>
 * <tr><td>121</td><td>Opposite MainLine 2 Trailing Volume</td></tr>
 * <tr><td>122</td><td>Opposite MainLine 2 Trailing Occupancy(MSB)</td></tr>
 * <tr><td>123</td><td>Opposite MainLine 2 Trailing Occupancy(LSB)</td></tr>
 * <tr><td>124</td><td>Opposite MainLine 2 Trailing Status</td></tr>
 * <tr><td>125</td><td>Opposite MainLine 2 Lane Status</td></tr>
 * <tr><td>126</td><td>Opposite MainLine 3 Speed</td></tr>
 * <tr><td>127</td><td>Opposite MainLine 3 Leading Volume</td></tr>
 * <tr><td>128</td><td>Opposite MainLine 3 Leading Occupancy(MSB)</td></tr>
 * <tr><td>129</td><td>Opposite MainLine 3 Leading Occupancy(LSB)</td></tr>
 * <tr><td>130</td><td>Opposite MainLine 3 Leading Status</td></tr>
 * <tr><td>131</td><td>Opposite MainLine 3 Trailing Volume</td></tr>
 * <tr><td>132</td><td>Opposite MainLine 3 Trailing Occupancy(MSB)</td></tr>
 * <tr><td>133</td><td>Opposite MainLine 3 Trailing Occupancy(LSB)</td></tr>
 * <tr><td>134</td><td>Opposite MainLine 3 Trailing Status</td></tr>
 * <tr><td>135</td><td>Opposite MainLine 3 Lane Status</td></tr>
 * <tr><td>136</td><td>Opposite MainLine 4 Speed</td></tr>
 * <tr><td>137</td><td>Opposite MainLine 4 Leading Volume</td></tr>
 * <tr><td>138</td><td>Opposite MainLine 4 Leading Occupancy(MSB)</td></tr>
 * <tr><td>139</td><td>Opposite MainLine 4 Leading Occupancy(LSB)</td></tr>
 * <tr><td>140</td><td>Opposite MainLine 4 Leading Status</td></tr>
 * <tr><td>141</td><td>Opposite MainLine 4 Trailing Volume</td></tr>
 * <tr><td>142</td><td>Opposite MainLine 4 Trailing Occupancy(MSB)</td></tr>
 * <tr><td>143</td><td>Opposite MainLine 4 Trailing Occupancy(LSB)</td></tr>
 * <tr><td>144</td><td>Opposite MainLine 4 Trailing Status</td></tr>
 * <tr><td>145</td><td>Opposite MainLine 4 Lane Status</td></tr>
 * <tr><td>146</td><td>Opposite MainLine 5 Speed</td></tr>
 * <tr><td>147</td><td>Opposite MainLine 5 Leading Volume</td></tr>
 * <tr><td>148</td><td>Opposite MainLine 5 Leading Occupancy(MSB)</td></tr>
 * <tr><td>149</td><td>Opposite MainLine 5 Leading Occupancy(LSB)</td></tr>
 * <tr><td>150</td><td>Opposite MainLine 5 Leading Status</td></tr>
 * <tr><td>151</td><td>Opposite MainLine 5 Trailing Volume</td></tr>
 * <tr><td>152</td><td>Opposite MainLine 5 Trailing Occupancy(MSB)</td></tr>
 * <tr><td>153</td><td>Opposite MainLine 5 Trailing Occupancy(LSB)</td></tr>
 * <tr><td>154</td><td>Opposite MainLine 5 Trailing Status</td></tr>
 * <tr><td>155</td><td>Opposite MainLine 5 Lane Status</td></tr>
 * <tr><td>156</td><td>Opposite MainLine 6 Speed</td></tr>
 * <tr><td>157</td><td>Opposite MainLine 6 Leading Volume</td></tr>
 * <tr><td>158</td><td>Opposite MainLine 6 Leading Occupancy(MSB)</td></tr>
 * <tr><td>159</td><td>Opposite MainLine 6 Leading Occupancy(LSB)</td></tr>
 * <tr><td>160</td><td>Opposite MainLine 6 Leading Status</td></tr>
 * <tr><td>161</td><td>Opposite MainLine 6 Trailing Volume</td></tr>
 * <tr><td>162</td><td>Opposite MainLine 6 Trailing Occupancy(MSB)</td></tr>
 * <tr><td>163</td><td>Opposite MainLine 6 Trailing Occupancy(LSB)</td></tr>
 * <tr><td>164</td><td>Opposite MainLine 6 Trailing Status</td></tr>
 * <tr><td>165</td><td>Opposite MainLine 6 Lane Status</td></tr>
 * <tr><td>166</td><td>Opposite MainLine 7 Speed</td></tr>
 * <tr><td>167</td><td>Opposite MainLine 7 Leading Volume</td></tr>
 * <tr><td>168</td><td>Opposite MainLine 7 Leading Occupancy(MSB)</td></tr>
 * <tr><td>169</td><td>Opposite MainLine 7 Leading Occupancy(LSB)</td></tr>
 * <tr><td>170</td><td>Opposite MainLine 7 Leading Status</td></tr>
 * <tr><td>171</td><td>Opposite MainLine 7 Trailing Volume</td></tr>
 * <tr><td>172</td><td>Opposite MainLine 7 Trailing Occupancy(MSB)</td></tr>
 * <tr><td>173</td><td>Opposite MainLine 7 Trailing Occupancy(LSB)</td></tr>
 * <tr><td>174</td><td>Opposite MainLine 7 Trailing Status</td></tr>
 * <tr><td>175</td><td>Opposite MainLine 7 Lane Status</td></tr>
 * <tr><td>176</td><td>Opposite MainLine 8 Speed</td></tr>
 * <tr><td>177</td><td>Opposite MainLine 8 Leading Volume</td></tr>
 * <tr><td>178</td><td>Opposite MainLine 8 Leading Occupancy(MSB)</td></tr>
 * <tr><td>179</td><td>Opposite MainLine 8 Leading Occupancy(LSB)</td></tr>
 * <tr><td>180</td><td>Opposite MainLine 8 Leading Status</td></tr>
 * <tr><td>181</td><td>Opposite MainLine 8 Trailing Volume</td></tr>
 * <tr><td>182</td><td>Opposite MainLine 8 Trailing Occupancy(MSB)</td></tr>
 * <tr><td>183</td><td>Opposite MainLine 8 Trailing Occupancy(LSB)</td></tr>
 * <tr><td>184</td><td>Opposite MainLine 8 Trailing Status</td></tr>
 * <tr><td>185</td><td>Opposite MainLine 8 Lane Status</td></tr>
 * <tr><td>186</td><td>Opposite Mainline Direction Bits, <br>
 *	(Each Lane 0=Normal, 1=Reverse)</td></tr>
 * <tr><td>187</td><td>Additional Detector 1 Volume</td></tr>
 * <tr><td>188</td><td>Additional Delector 1 Occupancy(MSB)</td></tr>
 * <tr><td>189</td><td>Additional Delector 1 Occupancy(MLB)</td></tr>
 * <tr><td>190</td><td>Additional Delector 1 Status</td></tr>
 * <tr><td>191</td><td>Additional Detector 2 Volume</td></tr>
 * <tr><td>192</td><td>Additional Delector 2 Occupancy(MSB)</td></tr>
 * <tr><td>193</td><td>Additional Delector 2 Occupancy(LSB)</td></tr>
 * <tr><td>194</td><td>Additional Delector 2 Status</td></tr>
 * <tr><td>195</td><td>Additional Detector 3 Volume</td></tr>
 * <tr><td>196</td><td>Additional Delector 3 Occupancy(MSB)</td></tr>
 * <tr><td>197</td><td>Additional Delector 3 Occupancy(LSB)</td></tr>
 * <tr><td>198</td><td>Additional Delector 4 Status</td></tr>
 * <tr><td>199</td><td>Additional Detector 4 Volume</td></tr>
 * <tr><td>200</td><td>Additional Delector 4 Occupancy(MSB)</td></tr>
 * <tr><td>201</td><td>Additional Delector 4 Occupancy(LSB)</td></tr>
 * <tr><td>202</td><td>Additional Delector 4 Status</td></tr>
 * <tr><td>203</td><td>Additional Detector 5 Volume</td></tr>
 * <tr><td>204</td><td>Additional Delector 5 Occupancy(MSB)</td></tr>
 * <tr><td>205</td><td>Additional Delector 5 Occupancy(LSB)</td></tr>
 * <tr><td>206</td><td>Additional Delector 5 Status</td></tr>
 * <tr><td>207</td><td>Additional Detector 6 Volume</td></tr>
 * <tr><td>208</td><td>Additional Delector 6 Occupancy(MSB)</td></tr>
 * <tr><td>209</td><td>Additional Delector 6 Occupancy(LSB)</td></tr>
 * <tr><td>210</td><td>Additional Delector 6 Status</td></tr>
 * <tr><td>211</td><td>Additional Detector 7 Volume</td></tr>
 * <tr><td>212</td><td>Additional Delector 7 Occupancy(MSB)</td></tr>
 * <tr><td>213</td><td>Additional Delector 7 Occupancy(LSB)</td></tr>
 * <tr><td>214</td><td>Additional Delector 7 Status</td></tr>
 * <tr><td>215</td><td>Additional Detector 8 Volume</td></tr>
 * <tr><td>216</td><td>Additional Delector 8 Occupancy(MSB)</td></tr>
 * <tr><td>217</td><td>Additional Delector 8 Occupancy(LSB)</td></tr>
 * <tr><td>218</td><td>Additional Delector 8 Status</td></tr>
 * <tr><td>219</td><td>Additional Detector 9 Volume</td></tr>
 * <tr><td>220</td><td>Additional Delector 9 Occupancy(MSB)</td></tr>
 * <tr><td>221</td><td>Additional Delector 9 Occupancy(LSB)</td></tr>
 * <tr><td>222</td><td>Additional Delector 9 Status</td></tr>
 * <tr><td>223</td><td>Additional Detector 10 Volume</td></tr>
 * <tr><td>224</td><td>Additional Delector 10 Occupancy(MSB)</td></tr>
 * <tr><td>225</td><td>Additional Delector 10 Occupancy(LSB)</td></tr>
 * <tr><td>226</td><td>Additional Delector 10 Status</td></tr>
 * <tr><td>227</td><td>Additional Detector 11 Volume</td></tr>
 * <tr><td>228</td><td>Additional Delector 11 Occupancy(MSB)</td></tr>
 * <tr><td>229</td><td>Additional Delector 11 Occupancy(LSB)</td></tr>
 * <tr><td>230</td><td>Additional Delector 11 Status</td></tr>
 * <tr><td>231</td><td>Additional Detector 12 Volume</td></tr>
 * <tr><td>232</td><td>Additional Delector 12 Occupancy(MSB)</td></tr>
 * <tr><td>233</td><td>Additional Delector 12 Occupancy(LSB)</td></tr>
 * <tr><td>234</td><td>Additional Delector 12 Status</td></tr>
 * <tr><td>235</td><td>Additional Detector 13 Volume</td></tr>
 * <tr><td>236</td><td>Additional Delector 13 Occupancy(MSB)</td></tr>
 * <tr><td>237</td><td>Additional Delector 13 Occupancy(LSB)</td></tr>
 * <tr><td>238</td><td>Additional Delector 13 Status</td></tr>
 * <tr><td>239</td><td>Additional Detector 14 Volume</td></tr>
 * <tr><td>240</td><td>Additional Delector 14 Occupancy(MSB)</td></tr>
 * <tr><td>241</td><td>Additional Delector 14 Occupancy(LSB)</td></tr>
 * <tr><td>242</td><td>Additional Delector 14 Status</td></tr>
 * <tr><td>243</td><td>Additional Detector 15 Volume</td></tr>
 * <tr><td>244</td><td>Additional Delector 15 Occupancy(MSB)</td></tr>
 * <tr><td>245</td><td>Additional Delector 15 Occupancy(LSB)</td></tr>
 * <tr><td>246</td><td>Additional Delector 15 Status</td></tr>
 * <tr><td>247</td><td>Additional Detector 16 Volume</td></tr>
 * <tr><td>248</td><td>Additional Delector 16 Occupancy(MSB)</td></tr>
 * <tr><td>249</td><td>Additional Delector 16 Occupancy(LSB)</td></tr>
 * <tr><td>250</td><td>Additional Delector 16 Status</td></tr>
 * <tr><td>251</td><td>Demand 1 Volume</td></tr>
 * <tr><td>252</td><td>Demand 1 Status</td></tr>
 * <tr><td>253</td><td>
 *	Passage 1 Volume, <br>
 *	Non Metering = 0, <br>
 *	Metering Startup = 1,<br>
 *	Metering = 2, <br>
 *	Metering Shutdown Active = 3, <br>
 *	Metering Shutdown = 4</td></tr>
 * <tr><td>254</td><td>Passage 1 Violations</td></tr>
 * <tr><td>255</td><td>Passage 1 Status</td></tr>
 * <tr><td>256</td><td>Metered Lane 1 Interval Zone</td></tr>
 * <tr><td>257</td><td>Metered Lane 1 Release Rate (MSB)</td></tr>
 * <tr><td>258</td><td>Metered Lane 1 Release Rate (LSB)</td></tr>
 * <tr><td>259</td><td>Demand 2 Volume</td></tr>
 * <tr><td>260</td><td>Demand 2 Status</td></tr>
 * <tr><td>261</td><td>Passage 2 Volume</td></tr>
 * <tr><td>262</td><td>Passage 2 Violations</td></tr>
 * <tr><td>263</td><td>Passage 2 Status</td></tr>
 * <tr><td>264</td><td>Metered Lane 2 Interval Zone</td></tr>
 * <tr><td>265</td><td>Metered Lane 2 Release Rate (MSB)</td></tr>
 * <tr><td>266</td><td>Metered Lane 2 Release Rate (LSB)</td></tr>
 * <tr><td>267</td><td>Demand 3 Volume</td></tr>
 * <tr><td>268</td><td>Demand 3 Status</td></tr>
 * <tr><td>269</td><td>Passage 3 Volume</td></tr>
 * <tr><td>270</td><td>Passage 3 Violations</td></tr>
 * <tr><td>271</td><td>Passage 3 Status</td></tr>
 * <tr><td>272</td><td>Metered Lane 3 Interval Zone</td></tr>
 * <tr><td>273</td><td>Metered Lane 3 Release Rate (MSB)</td></tr>
 * <tr><td>274</td><td>Metered Lane 3 Release Rate (LSB)</td></tr>
 * <tr><td>275</td><td>Demand 4 Volume</td></tr>
 * <tr><td>276</td><td>Demand 4 Status</td></tr>
 * <tr><td>277</td><td>Passage 4 Volume</td></tr>
 * <tr><td>278</td><td>Passage 4 Violations</td></tr>
 * <tr><td>279</td><td>Passage 4 Status</td></tr>
 * <tr><td>280</td><td>Metered Lane 4 Interval Zone</td></tr>
 * <tr><td>281</td><td>Metered Lane 4 Release Rate (MSB)</td></tr>
 * <tr><td>282</td><td>Metered Lane 4 Release Rate (LSB)</td></tr>
 * <tr><td>283</td><td>Is Metering (1 = YES)</td></tr>
 * <tr><td>284</td><td>Traffic Responsive Volume (VPH)(MSB)</td></tr>
 * <tr><td>285</td><td>Traffic Responsive Volume (VPH)(LSB)</td></tr>
 * <tr><td>286</td><td>Traffic Responsive Occupancy (VPH)(MSB)</td></tr>
 * <tr><td>287</td><td>Traffic Responsive Occupancy (VPH) (LSB), <br>
 *	each bit is one General Purpose output sign</td></tr>
 * <tr><td>288</td><td>Traffic Responsive Speed (VPH)</td></tr>
 * <tr><td>289</td><td>Spare</td></tr>
 * <tr><td>290</td><td>General Purpose Output Status</td></tr>
 * <tr><td>291</td><td>Metered Lane 1 Command Source</td></tr>
 * <tr><td>292</td><td>Metered Lane 1 Action</td></tr>
 * <tr><td>293</td><td>Metered Lane 1 Release Rate (MSB)</td></tr>
 * <tr><td>294</td><td>Metered Lane 1 Release Rate (LSB)</td></tr>
 * <tr><td>295</td><td>Metered Lane 1 Plan Number</td></tr>
 * <tr><td>296</td><td>Metered Lane 1 Plan Base Level</td></tr>
 * <tr><td>297</td><td>Metered Lane 1 Plan Adjustment Level</td></tr>
 * <tr><td>298</td><td>Metered Lane 1 Plan Final Level</td></tr>
 * <tr><td>299</td><td>Metered Lane 1 Base Meter Rate (MSB)</td></tr>
 * <tr><td>300</td><td>Metered Lane 1 Base Meter Rate  (LSB)</td></tr>
 * <tr><td>301</td><td>Metered Lane 1 Master Queue Flag</td></tr>
 * <tr><td>302</td><td>Metered Lane 2 Command Source</td></tr>
 * <tr><td>303</td><td>Metered Lane 2 Action</td></tr>
 * <tr><td>304</td><td>Metered Lane 2 Release Rate (MSB)</td></tr>
 * <tr><td>305</td><td>Metered Lane 2 Release Rate (LSB)</td></tr>
 * <tr><td>306</td><td>Metered Lane 2 Plan Number</td></tr>
 * <tr><td>307</td><td>Metered Lane 2 Plan Base Level</td></tr>
 * <tr><td>308</td><td>Metered Lane 2 Plan Adjustment Level</td></tr>
 * <tr><td>309</td><td>Metered Lane 2 Plan Final Level</td></tr>
 * <tr><td>310</td><td>Metered Lane 2 Base Meter Rate  (MSB)</td></tr>
 * <tr><td>311</td><td>Metered Lane 2 Base Meter Rate  (LSB)</td></tr>
 * <tr><td>312</td><td>Metered Lane 2 Master Queue Flag</td></tr>
 * <tr><td>313</td><td>Metered Lane 3 Command Source</td></tr>
 * <tr><td>314</td><td>Metered Lane 3 Action</td></tr>
 * <tr><td>315</td><td>Metered Lane 3 Release Rate (MSB)</td></tr>
 * <tr><td>316</td><td>Metered Lane 3 Release Rate (LSB)</td></tr>
 * <tr><td>317</td><td>Metered Lane 3 Plan Number</td></tr>
 * <tr><td>318</td><td>Metered Lane 3 Plan Base Level</td></tr>
 * <tr><td>319</td><td>Metered Lane 3 Plan Adjustment Level</td></tr>
 * <tr><td>320</td><td>Metered Lane 3 Plan Final Level</td></tr>
 * <tr><td>321</td><td>Metered Lane 3 Base Meter Rate  (MSB)</td></tr>
 * <tr><td>322</td><td>Metered Lane 3 Base Meter Rate  (LSB)</td></tr>
 * <tr><td>323</td><td>Metered Lane 3 Master Queue Flag</td></tr>
 * <tr><td>324</td><td>Metered Lane 4 Command Source</td></tr>
 * <tr><td>325</td><td>Metered Lane 4 Action</td></tr>
 * <tr><td>326</td><td>Metered Lane 4 Release Rate (MSB)</td></tr>
 * <tr><td>327</td><td>Metered Lane 4 Release Rate (LSB)</td></tr>
 * <tr><td>328</td><td>Metered Lane 4 Plan Number</td></tr>
 * <tr><td>329</td><td>Metered Lane 4 Plan Base Level</td></tr>
 * <tr><td>330</td><td>Metered Lane 4 Plan Adjustment Level</td></tr>
 * <tr><td>331</td><td>Metered Lane 4 Plan Final Level</td></tr>
 * <tr><td>332</td><td>Metered Lane 4 Base Meter Rate  (MSB)</td></tr>
 * <tr><td>333</td><td>Metered Lane 4 Base Meter Rate  (LSB)</td></tr>
 * <tr><td>334</td><td>Metered Lane 4 Master Queue Flag</td></tr>
 * <tr><td>335</td><td>Queue 1-1 Volume</td></tr>
 * <tr><td>336</td><td>Queue 1-1 Occupancy (MSB)</td></tr>
 * <tr><td>337</td><td>Queue 1-1 Occupancy (LSB)</td></tr>
 * <tr><td>338</td><td>Queue 1-1 Status</td></tr>
 * <tr><td>339</td><td>Queue 1-1 Queue Flag</td></tr>
 * <tr><td>340</td><td>Queue 2-1 Volume</td></tr>
 * <tr><td>341</td><td>Queue 2-1 Occupancy (MSB)</td></tr>
 * <tr><td>342</td><td>Queue 2-1 Occupancy (LSB)</td></tr>
 * <tr><td>343</td><td>Queue 2-1 Status</td></tr>
 * <tr><td>344</td><td>Queue 2-1 Queue Flag</td></tr>
 * <tr><td>345</td><td>Queue 3-1 Volume</td></tr>
 * <tr><td>346</td><td>Queue 3-1 Occupancy (MSB)</td></tr>
 * <tr><td>347</td><td>Queue 3-1 Occupancy (LSB)</td></tr>
 * <tr><td>348</td><td>Queue 3-1 Status</td></tr>
 * <tr><td>349</td><td>Queue 3-1 Queue Flag</td></tr>
 * <tr><td>350</td><td>Queue 4-1 Volume</td></tr>
 * <tr><td>351</td><td>Queue 4-1 Occupancy (MSB)</td></tr>
 * <tr><td>352</td><td>Queue 4-1 Occupancy (LSB)</td></tr>
 * <tr><td>353</td><td>Queue 4-1 Status</td></tr>
 * <tr><td>354</td><td>Queue 4-1 Queue Flag</td></tr>
 * <tr><td>355</td><td>Queue 1-2 Volume</td></tr>
 * <tr><td>356</td><td>Queue 1-2 Occupancy (MSB)</td></tr>
 * <tr><td>357</td><td>Queue 1-2 Occupancy (LSB)</td></tr>
 * <tr><td>358</td><td>Queue 1-2 Status</td></tr>
 * <tr><td>359</td><td>Queue 1-2 Queue Flag</td></tr>
 * <tr><td>360</td><td>Queue 2-2 Volume</td></tr>
 * <tr><td>361</td><td>Queue 2-2 Occupancy (MSB)</td></tr>
 * <tr><td>362</td><td>Queue 2-2 Occupancy (LSB)</td></tr>
 * <tr><td>363</td><td>Queue 2-2 Status</td></tr>
 * <tr><td>364</td><td>Queue 2-2 Queue Flag</td></tr>
 * <tr><td>365</td><td>Queue 3-2 Volume</td></tr>
 * <tr><td>366</td><td>Queue 3-2 Occupancy (MSB)</td></tr>
 * <tr><td>367</td><td>Queue 3-2 Occupancy (LSB)</td></tr>
 * <tr><td>368</td><td>Queue 3-2 Status</td></tr>
 * <tr><td>369</td><td>Queue 3-2 Queue Flag</td></tr>
 * <tr><td>370</td><td>Queue 4-2 Volume</td></tr>
 * <tr><td>371</td><td>Queue 4-2 Occupancy (MSB)</td></tr>
 * <tr><td>372</td><td>Queue 4-2 Occupancy (LSB)</td></tr>
 * <tr><td>373</td><td>Queue 4-2 Status</td></tr>
 * <tr><td>374</td><td>Queue 4-2 Queue Flag</td></tr>
 * <tr><td>375</td><td>Queue 1-3 Volume</td></tr>
 * <tr><td>376</td><td>Queue 1-3 Occupancy (MSB)</td></tr>
 * <tr><td>377</td><td>Queue 1-3 Occupancy (LSB)</td></tr>
 * <tr><td>378</td><td>Queue 1-3 Status</td></tr>
 * <tr><td>379</td><td>Queue 1-3 Queue Flag</td></tr>
 * <tr><td>380</td><td>Queue 2-3 Volume</td></tr>
 * <tr><td>381</td><td>Queue 2-3 Occupancy (MSB)</td></tr>
 * <tr><td>382</td><td>Queue 2-3 Occupancy (LSB)</td></tr>
 * <tr><td>383</td><td>Queue 2-3 Status</td></tr>
 * <tr><td>384</td><td>Queue 2-3 Queue Flag</td></tr>
 * <tr><td>385</td><td>Queue 3-3 Volume</td></tr>
 * <tr><td>386</td><td>Queue 3-3 Occupancy (MSB)</td></tr>
 * <tr><td>387</td><td>Queue 3-3 Occupancy (LSB)</td></tr>
 * <tr><td>388</td><td>Queue 3-3 Status</td></tr>
 * <tr><td>389</td><td>Queue 3-3 Queue Flag</td></tr>
 * <tr><td>390</td><td>Queue 4-3 Volume</td></tr>
 * <tr><td>391</td><td>Queue 4-3 Occupancy (MSB)</td></tr>
 * <tr><td>392</td><td>Queue 4-3 Occupancy (LSB)</td></tr>
 * <tr><td>393</td><td>Queue 4-3 Status</td></tr>
 * <tr><td>394</td><td>Queue 4-3 Queue Flag</td></tr>
 * <tr><td>395</td><td>Queue 1-4 Volume</td></tr>
 * <tr><td>396</td><td>Queue 1-4 Occupancy (MSB)</td></tr>
 * <tr><td>397</td><td>Queue 1-4 Occupancy (LSB)</td></tr>
 * <tr><td>398</td><td>Queue 1-4 Status</td></tr>
 * <tr><td>399</td><td>Queue 1-4 Queue Flag</td></tr>
 * <tr><td>400</td><td>Queue 2-4 Volume</td></tr>
 * <tr><td>401</td><td>Queue 2-4 Occupancy (MSB)</td></tr>
 * <tr><td>402</td><td>Queue 2-4 Occupancy (LSB)</td></tr>
 * <tr><td>403</td><td>Queue 2-4 Status</td></tr>
 * <tr><td>404</td><td>Queue 2-4 Queue Flag</td></tr>
 * <tr><td>405</td><td>Queue 3-4 Volume</td></tr>
 * <tr><td>406</td><td>Queue 3-4 Occupancy (MSB)</td></tr>
 * <tr><td>407</td><td>Queue 3-4 Occupancy (LSB)</td></tr>
 * <tr><td>408</td><td>Queue 3-4 Status</td></tr>
 * <tr><td>409</td><td>Queue 3-4 Queue Flag</td></tr>
 * <tr><td>410</td><td>Queue 4-4 Volume</td></tr>
 * <tr><td>411</td><td>Queue 4-4 Occupancy (MSB)</td></tr>
 * <tr><td>412</td><td>Queue 4-4 Occupancy (LSB)</td></tr>
 * <tr><td>413</td><td>Queue 4-4 Status</td></tr>
 * <tr><td>414</td><td>Queue 4-4 Queue Flag</td></tr>
 * <tr><td>415</td><td>CRC-16 Checksum (MSB)</td></tr>
 * <tr><td>416</td><td>CRC-16 Checksum (LSB)</td></tr>
 * </table>
 *
 * @author Michael Darter
 * @author Travis Swanston
 */
public class UrmsPoller extends MessagePoller implements SamplePoller {

	/** Debug log */
	static protected final DebugLog URMS_LOG = new DebugLog("urms");

	/** Log a msg */
	static protected void log(String msg) {
		if (URMS_LOG.isOpen())
			URMS_LOG.log(msg);
	}

	/** Minimum sensor ID */
	static protected final int SID_MIN = 1;

	/** Maximum sensor ID */
	static protected final int SID_MAX = 254;

	/** Associated CommLink */
	private final CommLinkImpl comm_link;

	/** Start reading once */
	private boolean started_reading = false;

	/** Timer gate, determines how often failed state of
	 * controllers is updated */
	private final TimedGate timer_gate = new TimedGate(1000);

	/** Constructor */
	public UrmsPoller(String n, Messenger m) {
		super(n, m);
		comm_link = (CommLinkImpl) CommLinkHelper.lookup(n);
		if (comm_link == null) {
			log("Failed to find CommLink.");
			return;
		}
		int to = comm_link.getTimeout();
		try {
			m.setTimeout(to);
			log("Set Messenger timeout to " + to + ".");
		}
		catch (IOException e) {
			log("Failed to set Messenger timeout.");
		}
		log("n=" + n + ", m=" + m + ", cl=" + comm_link);
	}

	/**
	 * Create a new message.
	 * @param o Ignored, because the operations are not associated
	 *          with controllers.
	 */
	@Override
	public CommMessage createCommMessageOp(Operation o) throws IOException {
		return new UrmsMessage(messenger.getOutputStream(null),
			messenger.getInputStream(null), timer_gate);
	}

	/**
	 * Is the drop address valid?
	 * @param drop The controller drop address, which is used to match the
	 *             URMS protocol sensor ID.
	 */
	@Override
	public boolean isAddressValid(int drop) {
		return ((drop >= SID_MIN) && (drop <= SID_MAX));
	}

	/** Reset controller */
	@Override
	public void resetController(ControllerImpl c) {
	}

	/** Send sample settings to a controller */
	@Override
	public void sendSettings(ControllerImpl c) {
	}

	/** Query sample data */
	@Override
	public void querySamples(ControllerImpl c, int i)  {
	}

	/** Query the sample poller */
	public void queryPoller() {
		if(started_reading)
			return;
		started_reading = true;
		log("Creating OpRead, cl=" + comm_link);
		addOperation(new OpRead(comm_link));
	}

	/** Sleep */
	static protected void sleepy() {
		sleepy(3797);			// FIXME: magic
	}

	/** Sleep */
	static protected void sleepy(int ms) {
		try {
			log("Sleeping " + ms);
			Thread.sleep(ms);
			log("Done sleeping");
		}
		catch(Exception e) {
			log("Sleep interupted ex=" + e);
		}
	}

	/** Get the protocol debug log */
	@Override
	protected DebugLog protocolLog() {
		return URMS_LOG;
	}

}
